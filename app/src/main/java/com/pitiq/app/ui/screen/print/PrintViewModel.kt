package com.pitiq.app.ui.screen.print

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pitiq.app.hardware.media.MediaProcessor
import com.pitiq.app.hardware.printer.PrintResult
import com.pitiq.app.hardware.printer.PrinterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class PrintUiState {
    data object Printing : PrintUiState()
    data object Success : PrintUiState()
    data class Failure(val message: String) : PrintUiState()
}

@HiltViewModel
class PrintViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val printerManager: PrinterManager,
    private val mediaProcessor: MediaProcessor,
) : ViewModel() {

    private val _state = MutableStateFlow<PrintUiState>(PrintUiState.Printing)
    val state: StateFlow<PrintUiState> = _state.asStateFlow()

    fun startPrint(sessionId: String) {
        _state.value = PrintUiState.Printing
        viewModelScope.launch {
            val colorFile = File(context.cacheDir, "session_$sessionId/color.png")
            val bitmap = BitmapFactory.decodeFile(colorFile.absolutePath)
            if (bitmap == null) {
                _state.value = PrintUiState.Failure("Could not load image for printing")
                return@launch
            }

            // Save thermal.png (grayscale, print-width)
            runCatching { mediaProcessor.renderThermalPng(sessionId, colorFile) }

            val result = printerManager.print(bitmap)
            bitmap.recycle()

            _state.value = if (result == PrintResult.Success) {
                PrintUiState.Success
            } else {
                PrintUiState.Failure(result.errorMessage)
            }
        }
    }
}
