package com.pitiq.app.ui.screen.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pitiq.app.domain.model.Layout

// TODO Phase 3.3: scrollable layout gallery, WindowSizeClass adaptive grid
@Composable
fun LayoutSelectionScreen(onLayoutConfirmed: (Layout) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text("Select a layout", color = MaterialTheme.colorScheme.onBackground)
    }
}
