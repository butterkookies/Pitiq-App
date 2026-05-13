package com.pitiq.app.ui.screen.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pitiq.app.hardware.bluetooth.BluetoothState
import com.pitiq.app.hardware.bluetooth.CoinAcceptorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val coinAcceptorRepository: CoinAcceptorRepository,
) : ViewModel() {

    val coinTotal: StateFlow<Int> = coinAcceptorRepository.coinTotal
    val bluetoothState: StateFlow<BluetoothState> = coinAcceptorRepository.bluetoothState

    fun simulateCoin(amount: Int) = coinAcceptorRepository.simulateCoin(amount)

    private val _timeoutCancelled = MutableStateFlow(false)
    val timeoutCancelled: StateFlow<Boolean> = _timeoutCancelled.asStateFlow()

    private var timerPaused = false

    init {
        coinAcceptorRepository.resetForNewSession()

        // 90-second idle timeout: only cancels session if no coins were inserted.
        viewModelScope.launch {
            var remaining = 90
            while (remaining > 0) {
                delay(1_000)
                if (!timerPaused) remaining--
            }
            if (coinTotal.value == 0) _timeoutCancelled.value = true
        }

        // Pause/resume timer and auto-cancel on BT reconnect failure.
        viewModelScope.launch {
            bluetoothState.collect { state ->
                timerPaused = when (state) {
                    BluetoothState.Disconnected, BluetoothState.Connecting,
                    BluetoothState.HandshakePending -> true
                    else -> false
                }
                if (state == BluetoothState.ReconnectFailed) {
                    _timeoutCancelled.value = true
                }
            }
        }
    }
}
