package com.pitiq.app.ui.screen.attract

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// TODO Phase 3.1: add Lottie animation, printer status check, "Out of Service" overlay
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttractScreen(
    onTap: () -> Unit,
    onExitKiosk: (pin: String) -> Boolean,
) {
    var showExitDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onTap() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "pitiq",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "₱40 per session",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Tap to begin",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
            )
        }

        // Invisible long-press target in the top-right corner for operator exit.
        // 80dp touch area — large enough to be reliable, small enough to be unnoticed.
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showExitDialog = true },
                )
        )
    }

    if (showExitDialog) {
        OperatorExitDialog(
            onDismiss = { showExitDialog = false },
            onExit = { pin ->
                val success = onExitKiosk(pin)
                if (success) showExitDialog = false
                success
            },
        )
    }
}
