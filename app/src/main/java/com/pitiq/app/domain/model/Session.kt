package com.pitiq.app.domain.model

data class Session(
    val sessionId: String,
    val locationId: String,
    /** Coins inserted in integer ₱ (e.g. 40 = ₱40). */
    val coinsInserted: Int = 0,
    val selectedLayout: Layout? = null,
    val captures: List<CapturedPhoto> = emptyList(),
    /** Maps slot index → whether the retake for that slot has been used. */
    val retakeUsed: Map<Int, Boolean> = emptyMap(),
    val printSuccess: Boolean? = null,
    val uploadStatus: UploadStatus = UploadStatus.PENDING,
    val errorLog: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
)

data class CapturedPhoto(
    val slotIndex: Int,
    val finalImagePath: String,
    val burstFramePaths: List<String> = emptyList(),
)

enum class UploadStatus { PENDING, UPLOADED, FAILED }
