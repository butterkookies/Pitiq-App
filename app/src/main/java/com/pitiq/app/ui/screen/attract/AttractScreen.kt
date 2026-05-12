package com.pitiq.app.ui.screen.attract

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// TODO Phase 3.1: add Lottie animation, printer status check, "Out of Service" overlay
@Composable
fun AttractScreen(
    locationId: String = "default",
    onTap: (locationId: String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onTap(locationId) },
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
    }
}
