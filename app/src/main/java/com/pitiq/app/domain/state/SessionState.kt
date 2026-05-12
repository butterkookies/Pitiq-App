package com.pitiq.app.domain.state

import com.pitiq.app.domain.model.CapturedPhoto
import com.pitiq.app.domain.model.Layout

sealed class SessionState {

    /** No active session — attract screen is shown. */
    data object Idle : SessionState()

    /** Customer has tapped the attract screen; session ID generated; collecting coins. */
    data class Payment(val sessionId: String) : SessionState()

    /** ₱40 collected; customer selects a layout. */
    data class LayoutSelection(val sessionId: String) : SessionState()

    /** Customer is being photographed for the given slot. */
    data class PhotoCapture(
        val sessionId: String,
        val layout: Layout,
        val currentSlot: Int,
        val isRetake: Boolean = false,
    ) : SessionState()

    /** All slots captured; customer edits the canvas. */
    data class Edit(
        val sessionId: String,
        val layout: Layout,
        val captures: List<CapturedPhoto>,
        val retakeUsed: Map<Int, Boolean>,
    ) : SessionState()

    /** Sending bitmap to thermal printer. */
    data class Print(val sessionId: String) : SessionState()

    /** Assembling GIF + uploading assets to Supabase. */
    data class Upload(val sessionId: String) : SessionState()

    /** Upload done; QR code displayed for 60 seconds. */
    data class QRShare(val sessionId: String, val shareUrl: String) : SessionState()
}
