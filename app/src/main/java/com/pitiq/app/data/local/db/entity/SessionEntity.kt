package com.pitiq.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upload_queue")
data class SessionEntity(
    @PrimaryKey val sessionId: String,
    val locationId: String,
    val coinsInserted: Int,
    val thermalImagePath: String?,
    val colorImagePath: String?,
    val gifPath: String?,
    val uploadStatus: String = "pending",
    val uploadAttemptedAt: Long? = null,
    val errorLog: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
)
