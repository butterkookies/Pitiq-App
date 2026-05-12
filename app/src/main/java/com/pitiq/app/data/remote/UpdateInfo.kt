package com.pitiq.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfo(
    @SerialName("version")     val version: Int,
    @SerialName("versionName") val versionName: String,
    @SerialName("apkUrl")      val apkUrl: String,
    @SerialName("changelog")   val changelog: String = "",
)
