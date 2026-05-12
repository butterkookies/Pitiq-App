package com.pitiq.app.ui.screen.upload

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pitiq.app.data.local.db.dao.UploadQueueDao
import com.pitiq.app.data.local.db.entity.SessionEntity
import com.pitiq.app.domain.model.Session
import com.pitiq.app.hardware.media.MediaProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed class UploadUiState {
    data object AssemblingGif : UploadUiState()
    data object Uploading : UploadUiState()
    data object Queued : UploadUiState()
    data class Done(val shareUrl: String) : UploadUiState()
    data class Error(val message: String) : UploadUiState()
}

@HiltViewModel
class UploadViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabaseClient: SupabaseClient,
    private val uploadQueueDao: UploadQueueDao,
    private val mediaProcessor: MediaProcessor,
) : ViewModel() {

    private val _state = MutableStateFlow<UploadUiState>(UploadUiState.AssemblingGif)
    val state: StateFlow<UploadUiState> = _state.asStateFlow()

    fun startUpload(session: Session) {
        viewModelScope.launch {
            _state.value = UploadUiState.AssemblingGif

            // Assemble GIF from burst frames
            runCatching {
                mediaProcessor.assembleGif(session.sessionId, session.captures)
            }

            _state.value = UploadUiState.Uploading

            val result = runCatching { upload(session) }
            if (result.isSuccess) {
                val shareUrl = result.getOrThrow()
                _state.value = UploadUiState.Done(shareUrl)
            } else {
                // Offline fallback: queue for later retry
                queueForRetry(session)
                _state.value = UploadUiState.Queued
            }
        }
    }

    private suspend fun upload(session: Session): String = withContext(Dispatchers.IO) {
        val sessionId = session.sessionId
        val bucket = supabaseClient.storage.from("sessions")

        suspend fun uploadFile(name: String): String {
            val file = File(context.cacheDir, "session_$sessionId/$name")
            if (!file.exists()) return ""
            bucket.upload("$sessionId/$name", file.readBytes()) { upsert = true }
            return bucket.createSignedUrl("$sessionId/$name", 1800.seconds)
        }

        val thermalUrl = uploadFile("thermal.png")
        val colorUrl = uploadFile("color.png")
        val gifUrl = uploadFile("session.gif")

        supabaseClient.postgrest.from("sessions").insert(
            buildJsonObject {
                put("session_id", sessionId)
                put("location_id", session.locationId)
                put("coins_inserted", session.coinsInserted)
                put("printed", session.printSuccess == true)
                put("upload_status", "uploaded")
                put("storage_urls", buildJsonObject {
                    put("thermal", thermalUrl)
                    put("color", colorUrl)
                    put("gif", gifUrl)
                }.toString())
            }
        )

        "https://pitiq.vercel.app/session/$sessionId"
    }

    private suspend fun queueForRetry(session: Session) {
        val base = File(context.cacheDir, "session_${session.sessionId}")
        uploadQueueDao.enqueue(
            SessionEntity(
                sessionId = session.sessionId,
                locationId = session.locationId,
                coinsInserted = session.coinsInserted,
                thermalImagePath = File(base, "thermal.png").takeIf { it.exists() }?.absolutePath,
                colorImagePath = File(base, "color.png").takeIf { it.exists() }?.absolutePath,
                gifPath = File(base, "session.gif").takeIf { it.exists() }?.absolutePath,
                uploadStatus = "pending",
                createdAt = session.createdAt,
            )
        )
    }
}
