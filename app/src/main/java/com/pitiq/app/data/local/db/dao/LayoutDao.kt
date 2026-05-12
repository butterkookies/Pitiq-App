package com.pitiq.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pitiq.app.data.local.db.entity.LayoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LayoutDao {
    @Query("SELECT * FROM layout_cache WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveLayouts(): Flow<List<LayoutEntity>>

    @Query("SELECT * FROM layout_cache")
    suspend fun getAll(): List<LayoutEntity>

    @Upsert
    suspend fun upsertAll(layouts: List<LayoutEntity>)

    @Query("DELETE FROM layout_cache WHERE id = :id")
    suspend fun deleteById(id: String)
}
