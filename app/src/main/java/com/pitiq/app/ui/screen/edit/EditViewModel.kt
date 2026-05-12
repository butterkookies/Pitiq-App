package com.pitiq.app.ui.screen.edit

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pitiq.app.domain.model.CapturedPhoto
import com.pitiq.app.domain.model.Layout
import com.pitiq.app.hardware.media.MediaProcessor
import com.pitiq.app.hardware.media.SlotTransform
import com.pitiq.app.hardware.media.canvasHeight
import com.pitiq.app.hardware.media.slotRects
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class EditSlotState(
    val panOffset: Offset = Offset.Zero,
    val scale: Float = 1f,
    val flipHorizontal: Boolean = false,
)

data class EditUiState(
    val selectedSlot: Int = 0,
    val slotStates: Map<Int, EditSlotState> = emptyMap(),
    val textFieldValues: Map<String, String> = emptyMap(),
    val timeRemaining: Int = 90,
    val timerActive: Boolean = true,
    val showTimeUpMessage: Boolean = false,
    val isRendering: Boolean = false,
)

@HiltViewModel
class EditViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaProcessor: MediaProcessor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (isActive && _uiState.value.timeRemaining > 0 && _uiState.value.timerActive) {
                delay(1_000)
                _uiState.update { it.copy(timeRemaining = (it.timeRemaining - 1).coerceAtLeast(0)) }
            }
            if (_uiState.value.timeRemaining == 0 && _uiState.value.timerActive) {
                _uiState.update { it.copy(showTimeUpMessage = true) }
            }
        }
    }

    fun selectSlot(index: Int) = _uiState.update { it.copy(selectedSlot = index) }

    fun flipSlot(index: Int) = _uiState.update { state ->
        val current = state.slotStates[index] ?: EditSlotState()
        state.copy(slotStates = state.slotStates + (index to current.copy(flipHorizontal = !current.flipHorizontal)))
    }

    fun panSlot(index: Int, delta: Offset) = _uiState.update { state ->
        val current = state.slotStates[index] ?: EditSlotState()
        state.copy(slotStates = state.slotStates + (index to current.copy(panOffset = current.panOffset + delta)))
    }

    fun zoomSlot(index: Int, scaleDelta: Float) = _uiState.update { state ->
        val current = state.slotStates[index] ?: EditSlotState()
        val newScale = (current.scale * scaleDelta).coerceIn(0.5f, 3f)
        state.copy(slotStates = state.slotStates + (index to current.copy(scale = newScale)))
    }

    fun setTextField(fieldId: String, value: String) = _uiState.update {
        it.copy(textFieldValues = it.textFieldValues + (fieldId to value))
    }

    fun pauseTimer() = _uiState.update { it.copy(timerActive = false) }

    fun resumeTimer(resetTo: Int = 90) {
        _uiState.update { it.copy(timerActive = true, timeRemaining = resetTo) }
        startTimer()
    }

    fun requestPrint(
        sessionId: String,
        captures: List<CapturedPhoto>,
        layout: Layout,
        onReady: () -> Unit,
    ) {
        _uiState.update { it.copy(isRendering = true) }
        viewModelScope.launch {
            val transforms = _uiState.value.slotStates.mapValues { (_, s) ->
                SlotTransform(
                    panOffset = PointF(s.panOffset.x, s.panOffset.y),
                    scale = s.scale,
                    flipHorizontal = s.flipHorizontal,
                )
            }
            runCatching {
                mediaProcessor.renderColorPng(sessionId, captures, layout, transforms)
            }
            _uiState.update { it.copy(isRendering = false) }
            onReady()
        }
    }
}
