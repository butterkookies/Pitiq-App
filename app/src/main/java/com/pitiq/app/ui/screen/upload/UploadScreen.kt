package com.pitiq.app.ui.screen.upload

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pitiq.app.domain.model.Session

@Composable
fun UploadScreen(
    session: Session,
    onUploadComplete: (shareUrl: String) -> Unit,
    onUploadFailed: (error: String) -> Unit,
    viewModel: UploadViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.startUpload(session) }

    LaunchedEffect(state) {
        when (val s = state) {
            is UploadUiState.Done -> onUploadComplete(s.shareUrl)
            is UploadUiState.Queued -> onUploadComplete("offline://${session.sessionId}")
            is UploadUiState.Error -> onUploadFailed(s.message)
            else -> Unit
        }
    }

    val pulse = rememberInfiniteTransition(label = "uploadPulse")
    val dotAlpha by pulse.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dotAlpha",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (state) {
                    is UploadUiState.AssemblingGif -> "Assembling your GIF…"
                    is UploadUiState.Uploading -> "Uploading your photos…"
                    is UploadUiState.Queued -> "Queued for upload"
                    else -> "Preparing your link…"
                },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "• • •",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                modifier = Modifier.alpha(dotAlpha),
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (state is UploadUiState.Queued) {
                Text(
                    text = "Your link is being prepared\nPlease stay nearby",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                )
            }
        }
    }
}
