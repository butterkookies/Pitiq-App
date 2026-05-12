package com.pitiq.app.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionCleaner @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun clean(sessionId: String) {
        File(context.cacheDir, "session_$sessionId").deleteRecursively()
    }
}
