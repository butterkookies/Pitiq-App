package com.pitiq.app.ui.screen.edit

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pitiq.app.domain.model.CapturedPhoto
import com.pitiq.app.domain.model.Layout
import com.pitiq.app.hardware.media.slotRects

@Composable
fun EditScreen(
    sessionId: String,
    layout: Layout,
    captures: List<CapturedPhoto>,
    retakeUsed: Map<Int, Boolean>,
    onPrintRequested: () -> Unit,
    onRetakeRequested: (slotIndex: Int) -> Unit,
    viewModel: EditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var editingTextField by remember { mutableStateOf<com.pitiq.app.domain.model.TextField?>(null) }

    val bitmaps = remember(captures) {
        captures.associate { photo ->
            photo.slotIndex to runCatching { BitmapFactory.decodeFile(photo.finalImagePath) }.getOrNull()
        }
    }

    LaunchedEffect(uiState.showTimeUpMessage) {
        if (uiState.showTimeUpMessage) {
            kotlinx.coroutines.delay(3_000)
            viewModel.requestPrint(sessionId, captures, layout, onPrintRequested)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Canvas: renders photos and handles gestures
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(uiState.selectedSlot) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (zoom != 1f) viewModel.zoomSlot(uiState.selectedSlot, zoom)
                        if (pan != Offset.Zero) viewModel.panSlot(uiState.selectedSlot, pan)
                    }
                }
                .pointerInput(layout) {
                    detectTapGestures(onTap = { tapOffset ->
                        val rects = layout.slotRects(size.width, size.height)
                        rects.forEachIndexed { i, rect ->
                            if (rect.contains(tapOffset.x, tapOffset.y)) {
                                viewModel.selectSlot(i)
                                return@detectTapGestures
                            }
                        }
                    })
                },
        ) {
            val slotRects = layout.slotRects(size.width.toInt(), size.height.toInt())
            slotRects.forEachIndexed { i, rect ->
                drawRect(
                    color = Color(0xFF1A1A1A),
                    topLeft = Offset(rect.left, rect.top),
                    size = Size(rect.width(), rect.height()),
                )
                val bmp = bitmaps[i]
                if (bmp != null) {
                    drawPhotoBitmap(bmp, rect, uiState.slotStates[i] ?: EditSlotState())
                }
                if (i == uiState.selectedSlot) {
                    drawRect(
                        color = Color(0xFFE8C97E),
                        topLeft = Offset(rect.left, rect.top),
                        size = Size(rect.width(), rect.height()),
                        style = Stroke(width = 3f),
                    )
                }
            }
        }

        // Timer
        Text(
            text = if (uiState.showTimeUpMessage) "Time's up!" else "⏱ ${uiState.timeRemaining}s",
            color = if (uiState.timeRemaining <= 10) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
        )

        // Per-slot controls
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xCC1C1C1C), RoundedCornerShape(8.dp))
                    .clickable { viewModel.flipSlot(uiState.selectedSlot) },
                contentAlignment = Alignment.Center,
            ) {
                Text("↔", color = Color.White, fontSize = 20.sp)
            }
            if (retakeUsed[uiState.selectedSlot] != true) {
                Box(
                    modifier = Modifier
                        .background(Color(0xCC1C1C1C), RoundedCornerShape(8.dp))
                        .clickable {
                            viewModel.pauseTimer()
                            onRetakeRequested(uiState.selectedSlot)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text("Retake", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                }
            }
        }

        // Text field chips
        if (layout.textFields.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                layout.textFields.forEach { field ->
                    val value = uiState.textFieldValues[field.id].orEmpty()
                    Text(
                        text = if (value.isEmpty()) "+ ${field.label}" else value,
                        color = if (value.isEmpty()) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .background(Color(0xCC1C1C1C), RoundedCornerShape(16.dp))
                            .clickable { editingTextField = field }
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
            }
        }

        Button(
            onClick = { if (!uiState.isRendering) viewModel.requestPrint(sessionId, captures, layout, onPrintRequested) },
            enabled = !uiState.isRendering,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = if (uiState.isRendering) "Preparing…" else "Print Now",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    editingTextField?.let { field ->
        var inputValue by remember(field.id) {
            mutableStateOf(uiState.textFieldValues[field.id].orEmpty())
        }
        AlertDialog(
            onDismissRequest = { editingTextField = null },
            title = { Text(field.label) },
            text = {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { if (it.length <= field.maxLength) inputValue = it },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setTextField(field.id, inputValue)
                    editingTextField = null
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { editingTextField = null }) { Text("Cancel") } },
        )
    }
}

private fun DrawScope.drawPhotoBitmap(
    bmp: android.graphics.Bitmap,
    rect: android.graphics.RectF,
    transform: EditSlotState,
) {
    val imageBitmap = bmp.asImageBitmap()
    val baseScale = maxOf(rect.width() / bmp.width, rect.height() / bmp.height) * transform.scale
    val dx = rect.left + (rect.width() - bmp.width * baseScale) / 2f + transform.panOffset.x
    val dy = rect.top + (rect.height() - bmp.height * baseScale) / 2f + transform.panOffset.y

    withTransform({
        clipRect(rect.left, rect.top, rect.right, rect.bottom)
        if (transform.flipHorizontal) {
            translate(left = dx + bmp.width * baseScale, top = dy)
            scale(scaleX = -baseScale, scaleY = baseScale, pivot = Offset.Zero)
        } else {
            translate(left = dx, top = dy)
            scale(scaleX = baseScale, scaleY = baseScale, pivot = Offset.Zero)
        }
    }) {
        drawImage(imageBitmap)
    }
}
