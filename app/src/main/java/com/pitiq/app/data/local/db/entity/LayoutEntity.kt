package com.pitiq.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "layout_cache")
data class LayoutEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slotCount: Int,
    val frameAssetUrl: String = "",
    val previewUrl: String = "",
    val textFieldsJson: String = "[]",
    val version: Int = 1,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
)
