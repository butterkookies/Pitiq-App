package com.pitiq.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Layout(
    val id: String,
    val name: String,
    val slotCount: Int,
    val frameAssetPath: String,
    val previewImagePath: String,
    val textFields: List<TextField> = emptyList(),
    val version: Int,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
)

@Serializable
data class TextField(
    val id: String,
    val label: String,
    val x: Float,
    val y: Float,
    val maxLength: Int = 40,
)
