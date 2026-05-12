package com.pitiq.app.data.update

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.pitiq.app.BuildConfig
import com.pitiq.app.data.remote.UpdateInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

sealed class UpdateState {
    data object None : UpdateState()
    data class Available(val info: UpdateInfo) : UpdateState()
    data object Downloading : UpdateState()
    data class ReadyToInstall(val apkFile: File, val info: UpdateInfo) : UpdateState()
}

@Singleton
class UpdateChecker @Inject constructor(
    private val httpClient: HttpClient,
    @ApplicationContext private val context: Context,
) {
    private val _state = MutableStateFlow<UpdateState>(UpdateState.None)
    val state: StateFlow<UpdateState> = _state.asStateFlow()

    suspend fun check() {
        val url = BuildConfig.UPDATE_JSON_URL
        if (url.isBlank()) return

        runCatching {
            val info = httpClient.get(url).body<UpdateInfo>()
            if (info.version > BuildConfig.VERSION_CODE) {
                _state.value = UpdateState.Available(info)
            }
        }
    }

    suspend fun downloadAndInstall(info: UpdateInfo) = withContext(Dispatchers.IO) {
        if (info.apkUrl.isBlank()) return@withContext
        _state.value = UpdateState.Downloading

        runCatching {
            val apkFile = File(context.getExternalFilesDir(null), "pitiq-update-${info.versionName}.apk")
            apkFile.writeBytes(httpClient.get(info.apkUrl).bodyAsBytes())
            _state.value = UpdateState.ReadyToInstall(apkFile, info)
            triggerInstall(apkFile)
        }.onFailure {
            _state.value = UpdateState.Available(info)
        }
    }

    fun dismiss() {
        _state.value = UpdateState.None
    }

    private fun triggerInstall(apkFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile,
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
