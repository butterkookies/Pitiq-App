package com.pitiq.app.data.repository

import com.pitiq.app.data.local.db.dao.LayoutDao
import com.pitiq.app.domain.model.Layout
import com.pitiq.app.domain.model.TextField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LayoutRepository @Inject constructor(
    private val layoutDao: LayoutDao,
) {
    companion object {
        val defaults: List<Layout> = listOf(
            Layout(
                id = "default-2slot",
                name = "Classic Strip",
                slotCount = 2,
                frameAssetPath = "",
                previewImagePath = "",
                version = 1,
                isDefault = true,
                sortOrder = 0,
            ),
            Layout(
                id = "default-4slot",
                name = "Photo Strip",
                slotCount = 4,
                frameAssetPath = "",
                previewImagePath = "",
                version = 1,
                isDefault = true,
                sortOrder = 1,
            ),
            Layout(
                id = "default-6slot",
                name = "Memory Grid",
                slotCount = 6,
                frameAssetPath = "",
                previewImagePath = "",
                version = 1,
                isDefault = true,
                sortOrder = 2,
            ),
        )
    }

    fun getLayouts(): Flow<List<Layout>> = layoutDao.getActiveLayouts().map { cached ->
        val remote = cached.map { entity ->
            Layout(
                id = entity.id,
                name = entity.name,
                slotCount = entity.slotCount,
                frameAssetPath = entity.frameAssetUrl,
                previewImagePath = entity.previewUrl,
                textFields = parseTextFields(entity.textFieldsJson),
                version = entity.version,
                isDefault = false,
                sortOrder = entity.sortOrder,
            )
        }
        val remoteIds = remote.map { it.id }.toSet()
        val mergedDefaults = defaults.filter { it.id !in remoteIds }
        (mergedDefaults + remote).sortedBy { it.sortOrder }
    }

    private fun parseTextFields(json: String): List<TextField> = runCatching {
        Json.decodeFromString<List<TextField>>(json)
    }.getOrDefault(emptyList())
}
