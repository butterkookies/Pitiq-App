package com.pitiq.app.ui.screen.print

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 3.6: print render pipeline, ESC/POS bitmap, USB OTG transfer, retry
@Composable
fun PrintScreen(
    onPrintSuccess: () -> Unit,
    onPrintFailed: (error: String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text("Printing…", color = MaterialTheme.colorScheme.onBackground)
    }
}
