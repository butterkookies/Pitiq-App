package com.pitiq.app.ui.screen.capture

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.pitiq.app.domain.model.CapturedPhoto
import com.pitiq.app.domain.model.Layout
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.Executors
import kotlin.coroutines.resume

@Composable
fun PhotoCaptureScreen(
    sessionId: String,
    layout: Layout,
    currentSlot: Int,
    isRetake: Boolean,
    onSlotCaptured: (CapturedPhoto) -> Unit,
    onRetakeComplete: (CapturedPhoto) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> cameraPermissionGranted = granted }

    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!cameraPermissionGranted) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Text("Camera permission required", color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var countdown by remember { mutableIntStateOf(10) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) { onDispose { executor.shutdown() } }

    // Camera setup
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { pv ->
                    val providerFuture = ProcessCameraProvider.getInstance(ctx)
                    providerFuture.addListener({
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(pv.surfaceProvider)
                        }
                        val ic = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = ic
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            ic,
                        )
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Slot indicator
        Text(
            text = if (isRetake) "Retake — Slot ${currentSlot + 1}"
            else "Photo ${currentSlot + 1} of ${layout.slotCount}",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
                .background(Color(0x88000000))
                .padding(horizontal = 20.dp, vertical = 8.dp),
        )

        // Countdown (shown only in final 3 seconds)
        AnimatedVisibility(
            visible = countdown in 1..3,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = "$countdown",
                color = Color.White,
                fontSize = 160.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // "Pose freely" hint during 4–10s window
        AnimatedVisibility(
            visible = countdown > 3,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
        ) {
            Text(
                text = "Smile!",
                color = Color.White,
                fontSize = 28.sp,
                modifier = Modifier
                    .background(Color(0x66000000))
                    .padding(horizontal = 32.dp, vertical = 12.dp),
            )
        }
    }

    // Capture sequence: runs once imageCapture is ready
    LaunchedEffect(imageCapture) {
        val ic = imageCapture ?: return@LaunchedEffect
        val burstPaths = mutableListOf<String>()
        val sessionDir = File(context.cacheDir, "session_$sessionId/slot_$currentSlot/burst")
        sessionDir.mkdirs()

        for (tick in 10 downTo 1) {
            countdown = tick
            val burstFile = File(sessionDir, "burst_${10 - tick}.jpg")
            val options = ImageCapture.OutputFileOptions.Builder(burstFile).build()
            val path = suspendCancellableCoroutine { cont ->
                ic.takePicture(options, executor, object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) =
                        cont.resume(burstFile.absolutePath)
                    override fun onError(e: ImageCaptureException) = cont.resume(null)
                })
            }
            if (path != null) burstPaths.add(path)
            delay(1_000)
        }

        countdown = 0

        // Use last captured frame as the final photo for this slot
        val finalSrc = burstPaths.lastOrNull() ?: return@LaunchedEffect
        val finalFile = File(context.cacheDir, "session_$sessionId/slot_$currentSlot/final.jpg")
        finalFile.parentFile?.mkdirs()
        File(finalSrc).copyTo(finalFile, overwrite = true)

        val photo = CapturedPhoto(
            slotIndex = currentSlot,
            finalImagePath = finalFile.absolutePath,
            burstFramePaths = burstPaths.toList(),
        )

        if (isRetake) onRetakeComplete(photo) else onSlotCaptured(photo)
    }
}
