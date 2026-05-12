package com.pitiq.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pitiq.app.data.local.db.dao.LayoutDao
import com.pitiq.app.data.local.db.dao.UploadQueueDao
import com.pitiq.app.data.local.db.entity.LayoutEntity
import com.pitiq.app.data.local.db.entity.SessionEntity

@Database(
    entities = [SessionEntity::class, LayoutEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class PitiqDatabase : RoomDatabase() {
    abstract fun uploadQueueDao(): UploadQueueDao
    abstract fun layoutDao(): LayoutDao
}
