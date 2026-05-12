package com.pitiq.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pitiq.app.data.local.db.dao.UploadQueueDao
import com.pitiq.app.data.local.db.entity.SessionEntity

@Database(
    entities = [SessionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PitiqDatabase : RoomDatabase() {
    abstract fun uploadQueueDao(): UploadQueueDao
}
