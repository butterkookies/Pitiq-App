package com.pitiq.app.ui.screen.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 3.7: GIF assembly, Supabase upload, progress indicator
@Composable
fun UploadScreen(
    onUploadComplete: (shareUrl: String) -> Unit,
    onUploadFailed: (error: String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text("Preparing your link…", color = MaterialTheme.colorScheme.onBackground)
    }
}
