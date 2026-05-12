package com.pitiq.app.hardware.bluetooth

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.bluetooth.BluetoothManager as AndroidBluetoothManager
import com.pitiq.app.data.local.prefs.SecurePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val RFCOMM_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
private const val RECONNECT_WINDOW_MS = 30_000L
private const val RECONNECT_INTERVAL_MS = 3_000L

/**
 * Manages the RFCOMM Bluetooth connection to the ESP32 coin acceptor.
 *
 * Protocol (line-delimited text, '\n'):
 *   App → ESP32: CHALLENGE:<16-byte hex>
 *   ESP32 → App: RESPONSE:<HMAC-SHA256 hex>
 *   ESP32 → App: COIN:<peso>          (e.g. COIN:10 = ₱10)
 *   App → ESP32: BUFFER_REQUEST       (sent after reconnect)
 *   ESP32 → App: BUFFER:<peso>        (buffered total while disconnected)
 *   ESP32 → App: DISCONNECT_ACK
 *
 * No coin pulses are accepted until the per-session HMAC handshake succeeds.
 * Requires BLUETOOTH_CONNECT (API 31+) or BLUETOOTH (API 23–30) permission at runtime.
 */
@Singleton
class BluetoothManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securePreferences: SecurePreferences,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow<BluetoothState>(BluetoothState.Disconnected)
    val state: StateFlow<BluetoothState> = _state.asStateFlow()

    private val _messages = MutableSharedFlow<BluetoothMessage>(extraBufferCapacity = 64)
    val messages: SharedFlow<BluetoothMessage> = _messages.asSharedFlow()

    private var socket: BluetoothSocket? = null
    private var writer: PrintWriter? = null
    private var readJob: Job? = null
    private var reconnectJob: Job? = null
    private var sessionChallenge: ByteArray? = null
    private var handshakeVerified = false

    /** Connect using the stored device address. No-op if no address is saved. */
    fun start() {
        val address = securePreferences.bluetoothDeviceAddress ?: return
        scope.launch { openConnection(address) }
    }

    /** Invalidate the current session token and start a fresh handshake. */
    fun startNewSession() {
        handshakeVerified = false
        sessionChallenge = null
        if (_state.value == BluetoothState.Connected) {
            sendChallenge()
            _state.value = BluetoothState.HandshakePending
        }
    }

    private suspend fun openConnection(address: String): Boolean {
        _state.value = BluetoothState.Connecting
        return try {
            val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as AndroidBluetoothManager
            val adapter = btManager.adapter
                ?: return false.also { _state.value = BluetoothState.Disconnected }
            val device = adapter.getRemoteDevice(address)
            val sock = device.createRfcommSocketToServiceRecord(RFCOMM_UUID)
            adapter.cancelDiscovery()
            sock.connect()
            socket = sock
            writer = PrintWriter(sock.outputStream, true)
            handshakeVerified = false
            _state.value = BluetoothState.HandshakePending
            startReadLoop(sock, address)
            sendChallenge()
            true
        } catch (_: IOException) {
            _state.value = BluetoothState.Disconnected
            false
        } catch (_: SecurityException) {
            // Bluetooth permission not yet granted; caller must request permission first.
            _state.value = BluetoothState.Disconnected
            false
        }
    }

    private fun startReadLoop(sock: BluetoothSocket, address: String) {
        readJob?.cancel()
        readJob = scope.launch {
            val reader = BufferedReader(InputStreamReader(sock.inputStream))
            try {
                while (isActive) {
                    val line = reader.readLine() ?: break
                    handleLine(line.trim())
                }
            } catch (_: IOException) {
                // Socket closed or remote end disconnected.
            } finally {
                onSocketLost(address)
            }
        }
    }

    private fun handleLine(line: String) {
        when {
            line.startsWith("RESPONSE:") -> {
                val hmacHex = line.removePrefix("RESPONSE:")
                val challenge = sessionChallenge ?: return
                val secret = securePreferences.bluetoothSharedSecret ?: return
                if (HmacVerifier.verify(challenge, secret, hmacHex)) {
                    handshakeVerified = true
                    _state.value = BluetoothState.Connected
                } else {
                    disconnect()
                }
            }
            line.startsWith("COIN:") && handshakeVerified -> {
                val amount = line.removePrefix("COIN:").toIntOrNull() ?: return
                scope.launch { _messages.emit(BluetoothMessage.CoinInserted(amount)) }
            }
            line.startsWith("BUFFER:") && handshakeVerified -> {
                val total = line.removePrefix("BUFFER:").toIntOrNull() ?: return
                scope.launch { _messages.emit(BluetoothMessage.CoinBufferResponse(total)) }
            }
            line == "DISCONNECT_ACK" -> {
                scope.launch { _messages.emit(BluetoothMessage.DisconnectAck) }
            }
        }
    }

    private fun sendChallenge() {
        val challenge = ByteArray(16).also { SecureRandom().nextBytes(it) }
        sessionChallenge = challenge
        writer?.println("CHALLENGE:${challenge.toHex()}")
    }

    /** Ask ESP32 for any coins buffered while the app was disconnected. */
    fun requestBufferedTotal() {
        if (handshakeVerified) writer?.println("BUFFER_REQUEST")
    }

    private fun onSocketLost(address: String) {
        _state.value = BluetoothState.Disconnected
        scheduleReconnect(address)
    }

    private fun scheduleReconnect(address: String) {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val deadline = System.currentTimeMillis() + RECONNECT_WINDOW_MS
            while (System.currentTimeMillis() < deadline) {
                delay(RECONNECT_INTERVAL_MS)
                if (openConnection(address)) {
                    requestBufferedTotal()
                    return@launch
                }
            }
            _state.value = BluetoothState.ReconnectFailed
        }
    }

    fun disconnect() {
        reconnectJob?.cancel()
        readJob?.cancel()
        try { socket?.close() } catch (_: IOException) {}
        socket = null
        writer = null
        handshakeVerified = false
        sessionChallenge = null
        _state.value = BluetoothState.Disconnected
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
