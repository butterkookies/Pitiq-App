package com.pitiq.app.session

import androidx.lifecycle.ViewModel
import com.pitiq.app.domain.model.CapturedPhoto
import com.pitiq.app.domain.model.Layout
import com.pitiq.app.domain.model.Session
import com.pitiq.app.domain.model.UploadStatus
import com.pitiq.app.domain.state.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor() : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Idle)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    fun initSession(locationId: String) {
        val id = UUID.randomUUID().toString()
        _session.value = Session(sessionId = id, locationId = locationId)
        _sessionState.value = SessionState.Payment(id)
    }

    fun onCoinInserted(totalCoins: Int) {
        _session.update { it?.copy(coinsInserted = totalCoins) }
    }

    fun onPaymentComplete() {
        val s = _session.value ?: return
        _sessionState.value = SessionState.LayoutSelection(s.sessionId)
    }

    fun onLayoutSelected(layout: Layout) {
        val s = _session.value ?: return
        _session.update { it?.copy(selectedLayout = layout) }
        _sessionState.value = SessionState.PhotoCapture(
            sessionId = s.sessionId,
            layout = layout,
            currentSlot = 0,
        )
    }

    fun onSlotCaptured(photo: CapturedPhoto) {
        val s = _session.value ?: return
        val layout = s.selectedLayout ?: return
        val updatedCaptures = s.captures + photo
        _session.update { it?.copy(captures = updatedCaptures) }

        val nextSlot = photo.slotIndex + 1
        if (nextSlot < layout.slotCount) {
            _sessionState.value = SessionState.PhotoCapture(
                sessionId = s.sessionId,
                layout = layout,
                currentSlot = nextSlot,
            )
        } else {
            _sessionState.value = SessionState.Edit(
                sessionId = s.sessionId,
                layout = layout,
                captures = updatedCaptures,
                retakeUsed = s.retakeUsed,
            )
        }
    }

    fun onRetakeRequested(slotIndex: Int) {
        val s = _session.value ?: return
        val layout = s.selectedLayout ?: return
        _sessionState.value = SessionState.PhotoCapture(
            sessionId = s.sessionId,
            layout = layout,
            currentSlot = slotIndex,
            isRetake = true,
        )
    }

    fun onRetakeComplete(photo: CapturedPhoto) {
        val s = _session.value ?: return
        val layout = s.selectedLayout ?: return
        val updatedCaptures = s.captures.map {
            if (it.slotIndex == photo.slotIndex) photo else it
        }
        val updatedRetakeUsed = s.retakeUsed + (photo.slotIndex to true)
        _session.update {
            it?.copy(captures = updatedCaptures, retakeUsed = updatedRetakeUsed)
        }
        _sessionState.value = SessionState.Edit(
            sessionId = s.sessionId,
            layout = layout,
            captures = updatedCaptures,
            retakeUsed = updatedRetakeUsed,
        )
    }

    fun onPrintRequested() {
        val s = _session.value ?: return
        _sessionState.value = SessionState.Print(s.sessionId)
    }

    fun onPrintSuccess() {
        val s = _session.value ?: return
        _session.update { it?.copy(printSuccess = true) }
        _sessionState.value = SessionState.Upload(s.sessionId)
    }

    fun onPrintFailed(error: String) {
        _session.update {
            it?.copy(printSuccess = false, errorLog = it.errorLog + error)
        }
    }

    fun onUploadComplete(shareUrl: String) {
        val s = _session.value ?: return
        _session.update { it?.copy(uploadStatus = UploadStatus.UPLOADED) }
        _sessionState.value = SessionState.QRShare(s.sessionId, shareUrl)
    }

    fun onUploadFailed(error: String) {
        _session.update {
            it?.copy(uploadStatus = UploadStatus.FAILED, errorLog = it.errorLog + error)
        }
    }

    fun cancelSession() {
        _session.value = null
        _sessionState.value = SessionState.Idle
    }

    fun resetToAttract() {
        _session.value = null
        _sessionState.value = SessionState.Idle
    }
}
