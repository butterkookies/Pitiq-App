package com.pitiq.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pitiq.app.data.local.db.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UploadQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(session: SessionEntity)

    @Query("SELECT * FROM upload_queue WHERE uploadStatus = 'pending' ORDER BY createdAt ASC")
    fun observePending(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM upload_queue WHERE uploadStatus = 'pending' ORDER BY createdAt ASC")
    suspend fun getPending(): List<SessionEntity>

    @Query("UPDATE upload_queue SET uploadStatus = :status, uploadAttemptedAt = :attemptedAt WHERE sessionId = :sessionId")
    suspend fun updateStatus(sessionId: String, status: String, attemptedAt: Long)

    @Query("DELETE FROM upload_queue WHERE sessionId = :sessionId")
    suspend fun delete(sessionId: String)
}
