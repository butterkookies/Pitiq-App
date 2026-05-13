package com.pitiq.app.ui.screen.attract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pitiq.app.BuildConfig
import com.pitiq.app.hardware.printer.PrinterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractViewModel @Inject constructor(
    private val printerManager: PrinterManager,
) : ViewModel() {

    private val _printerConnected = MutableStateFlow(true)
    val printerConnected: StateFlow<Boolean> = _printerConnected.asStateFlow()

    init {
        viewModelScope.launch {
            if (BuildConfig.DEBUG) return@launch  // skip printer gate in debug builds
            while (isActive) {
                _printerConnected.value = printerManager.isPrinterConnected()
                delay(10_000)
            }
        }
    }
}
