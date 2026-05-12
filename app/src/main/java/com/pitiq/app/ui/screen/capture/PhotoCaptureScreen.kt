package com.pitiq.app.ui.screen.capture

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pitiq.app.domain.model.CapturedPhoto

// TODO Phase 3.4: CameraX preview, layout overlay, 10s countdown, burst capture
@Composable
fun PhotoCaptureScreen(
    isRetake: Boolean = false,
    onSlotCaptured: (CapturedPhoto) -> Unit,
    onRetakeComplete: (CapturedPhoto) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text("Camera", color = MaterialTheme.colorScheme.onBackground)
    }
}
