package com.pitiq.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RemoteLayout(
    @SerialName("id")              val id: String,
    @SerialName("name")            val name: String,
    @SerialName("slot_count")      val slotCount: Int,
    @SerialName("frame_asset_url") val frameAssetUrl: String? = null,
    @SerialName("preview_url")     val previewUrl: String? = null,
    @SerialName("text_fields")     val textFields: JsonElement? = null,
    @SerialName("version")         val version: Int = 1,
    @SerialName("sort_order")      val sortOrder: Int = 0,
)
