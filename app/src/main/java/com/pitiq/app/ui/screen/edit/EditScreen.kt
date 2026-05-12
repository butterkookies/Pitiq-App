package com.pitiq.app.ui.screen.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO Phase 3.5: edit canvas, drag/zoom/flip, retake, text fields, 90s timer
@Composable
fun EditScreen(
    onPrintRequested: () -> Unit,
    onRetakeRequested: (slotIndex: Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text("Edit", color = MaterialTheme.colorScheme.onBackground)
    }
}
