package com.pitiq.app.di

import android.content.Context
import androidx.room.Room
import com.pitiq.app.data.local.db.PitiqDatabase
import com.pitiq.app.data.local.db.dao.UploadQueueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PitiqDatabase =
        Room.databaseBuilder(context, PitiqDatabase::class.java, "pitiq.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideUploadQueueDao(db: PitiqDatabase): UploadQueueDao = db.uploadQueueDao()
}
