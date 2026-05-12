package com.pitiq.app.ui.screen.attract

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttractScreen(
    onTap: () -> Unit,
    onExitKiosk: (pin: String) -> Boolean,
    viewModel: AttractViewModel = hiltViewModel(),
) {
    val printerConnected by viewModel.printerConnected.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "tapAlpha",
    )

    val ringTransition = rememberInfiniteTransition(label = "ring")
    val ringScale by ringTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.2f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringScale",
    )
    val ringAlpha by ringTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringAlpha",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(enabled = printerConnected) { onTap() },
        contentAlignment = Alignment.Center,
    ) {
        // Animated ring behind the logo
        Box(
            modifier = Modifier
                .size((220 * ringScale).dp)
                .alpha(ringAlpha * 0.15f)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = androidx.compose.foundation.shape.CircleShape,
                ),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "pitiq",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "₱40 per session",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
            )
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = "Tap anywhere to begin",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 18.sp,
                modifier = Modifier.alpha(pulseAlpha),
            )
        }

        // Invisible long-press target in the top-right corner for operator exit.
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

        // Out-of-service overlay when printer is disconnected.
        if (!printerConnected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xE6000000)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp),
                ) {
                    Text(
                        text = "OUT OF SERVICE",
                        color = Color(0xFFFF4444),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PRINTER NOT DETECTED",
                        color = Color(0xFFFFAAAA),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }

    if (showExitDialog) {
        OperatorExitDialog(
            onDismiss = { showExitDialog = false },
            onExit = { pin ->
                val ok = onExitKiosk(pin)
                if (ok) showExitDialog = false
                ok
            },
        )
    }
}
