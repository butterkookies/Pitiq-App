package com.pitiq.app.data.repository

import android.content.Context
import com.pitiq.app.data.local.db.dao.LayoutDao
import com.pitiq.app.data.local.db.entity.LayoutEntity
import com.pitiq.app.data.remote.RemoteLayout
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LayoutSyncManager @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val layoutDao: LayoutDao,
    private val httpClient: HttpClient,
    @ApplicationContext private val context: Context,
) {
    suspend fun sync() = withContext(Dispatchers.IO) {
        runCatching {
            val remoteLayouts = supabaseClient.postgrest
                .from("layouts")
                .select { filter { eq("active", true) } }
                .decodeList<RemoteLayout>()

            val localVersionMap = layoutDao.getAll().associate { it.id to it.version }

            val toUpdate = remoteLayouts.filter { remote ->
                val local = localVersionMap[remote.id]
                local == null || remote.version > local
            }

            if (toUpdate.isEmpty()) return@runCatching

            for (layout in toUpdate) {
                layout.frameAssetUrl?.let { downloadAsset(it, frameFile(layout.id)) }
                layout.previewUrl?.let { downloadAsset(it, previewFile(layout.id)) }
            }

            layoutDao.upsertAll(toUpdate.map { it.toEntity() })
        }
        // Silently ignore network errors — cached layouts remain in use
    }

    private suspend fun downloadAsset(url: String, dest: File) {
        runCatching {
            dest.parentFile?.mkdirs()
            dest.writeBytes(httpClient.get(url).bodyAsBytes())
        }
    }

    private fun frameFile(id: String) = File(context.filesDir, "layouts/${id}_frame.png")
    private fun previewFile(id: String) = File(context.filesDir, "layouts/${id}_preview.png")

    private fun RemoteLayout.toEntity() = LayoutEntity(
        id = id,
        name = name,
        slotCount = slotCount,
        frameAssetUrl = if (!frameAssetUrl.isNullOrBlank()) frameFile(id).absolutePath else "",
        previewUrl = if (!previewUrl.isNullOrBlank()) previewFile(id).absolutePath else "",
        textFieldsJson = textFields?.toString() ?: "[]",
        version = version,
        isActive = true,
        sortOrder = sortOrder,
    )
}
