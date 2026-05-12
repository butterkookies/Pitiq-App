package com.pitiq.app.ui.screen.qrshare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 3.7: QR code display, 60s countdown, auto-reset to Attract
@Composable
fun QRShareScreen(
    shareUrl: String,
    onExpired: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text("Scan to download your photos", color = MaterialTheme.colorScheme.onBackground)
    }
}
