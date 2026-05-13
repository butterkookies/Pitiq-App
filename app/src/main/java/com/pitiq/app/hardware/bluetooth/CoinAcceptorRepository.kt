package com.pitiq.app.hardware.bluetooth

import com.pitiq.app.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Accumulates coin totals from the Bluetooth coin acceptor and exposes them as a StateFlow.
 *
 * Coin amounts (₱1, ₱5, ₱10) arrive as [BluetoothMessage.CoinInserted] events.
 * Buffered totals from reconnect arrive as [BluetoothMessage.CoinBufferResponse].
 * Call [resetForNewSession] at the start of each session to clear the running total
 * and trigger a new per-session HMAC handshake on the BluetoothManager.
 */
@Singleton
class CoinAcceptorRepository @Inject constructor(
    private val bluetoothManager: BluetoothManager,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _coinTotal = MutableStateFlow(0)
    val coinTotal: StateFlow<Int> = _coinTotal.asStateFlow()

    val bluetoothState: StateFlow<BluetoothState> = bluetoothManager.state

    init {
        scope.launch {
            bluetoothManager.messages.collect { message ->
                when (message) {
                    is BluetoothMessage.CoinInserted ->
                        _coinTotal.update { it + message.amountPeso }
                    is BluetoothMessage.CoinBufferResponse ->
                        _coinTotal.update { it + message.totalPeso }
                    else -> Unit
                }
            }
        }
    }

    fun resetForNewSession() {
        _coinTotal.value = 0
        bluetoothManager.startNewSession()
    }

    fun simulateCoin(amount: Int) {
        if (!BuildConfig.DEBUG) return
        _coinTotal.update { it + amount }
    }
}
