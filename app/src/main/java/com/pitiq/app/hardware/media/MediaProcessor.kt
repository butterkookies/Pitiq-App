package com.pitiq.app.hardware.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import com.pitiq.app.domain.model.CapturedPhoto
import com.pitiq.app.domain.model.Layout
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val THERMAL_WIDTH = 384
        const val COLOR_SCALE = 3
        const val GIF_FRAME_WIDTH = 480
        const val GIF_DELAY_CENTISECONDS = 12 // ~8 fps
        const val MAX_GIF_SIZE_BYTES = 5_000_000L
    }

    /**
     * Renders the full-color composite image for all slots and saves it as color.png.
     * Returns the output File on success.
     */
    suspend fun renderColorPng(
        sessionId: String,
        captures: List<CapturedPhoto>,
        layout: Layout,
        slotTransforms: Map<Int, SlotTransform>,
    ): File = withContext(Dispatchers.Default) {
        val width = THERMAL_WIDTH * COLOR_SCALE
        val height = layout.canvasHeight() * COLOR_SCALE
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)

        val slots = layout.slotRects(width, height)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG)

        for (i in slots.indices) {
            val slot = slots.getOrNull(i) ?: continue
            val capture = captures.getOrNull(i) ?: continue
            val transform = slotTransforms[i] ?: SlotTransform()
            val photo = BitmapFactory.decodeFile(capture.finalImagePath) ?: continue

            canvas.save()
            canvas.clipRect(slot)
            val matrix = buildPhotoMatrix(slot, photo, transform)
            canvas.drawBitmap(photo, matrix, paint)
            photo.recycle()
            canvas.restore()
        }

        val outFile = sessionFile(sessionId, "color.png")
        outFile.parentFile?.mkdirs()
        outFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        outFile
    }

    /**
     * Renders a grayscale thermal-width bitmap and saves as thermal.png.
     * Returns the output File on success.
     */
    suspend fun renderThermalPng(
        sessionId: String,
        colorPngFile: File,
    ): File = withContext(Dispatchers.Default) {
        val source = BitmapFactory.decodeFile(colorPngFile.absolutePath)
            ?: error("color.png not found")
        val scaled = Bitmap.createScaledBitmap(source, THERMAL_WIDTH,
            (source.height * THERMAL_WIDTH.toFloat() / source.width).toInt(), true)
        source.recycle()

        val grayscale = Bitmap.createBitmap(scaled.width, scaled.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscale)
        val paint = Paint().apply {
            colorFilter = android.graphics.ColorMatrixColorFilter(
                android.graphics.ColorMatrix().also { it.setSaturation(0f) }
            )
        }
        canvas.drawBitmap(scaled, 0f, 0f, paint)
        scaled.recycle()

        val outFile = sessionFile(sessionId, "thermal.png")
        outFile.outputStream().use { grayscale.compress(Bitmap.CompressFormat.PNG, 100, it) }
        grayscale.recycle()
        outFile
    }

    /**
     * Assembles an animated GIF from all burst frames.
     * Returns the output File on success, or null if no frames found.
     */
    suspend fun assembleGif(
        sessionId: String,
        captures: List<CapturedPhoto>,
    ): File? = withContext(Dispatchers.Default) {
        val allBursts = captures.flatMap { it.burstFramePaths }
        if (allBursts.isEmpty()) return@withContext null

        val frames = allBursts.mapNotNull { path ->
            BitmapFactory.decodeFile(path)?.let { bmp ->
                val targetHeight = (bmp.height * GIF_FRAME_WIDTH.toFloat() / bmp.width).toInt()
                val scaled = Bitmap.createScaledBitmap(bmp, GIF_FRAME_WIDTH, targetHeight, true)
                bmp.recycle()
                scaled
            }
        }
        if (frames.isEmpty()) return@withContext null

        val outFile = sessionFile(sessionId, "session.gif")
        outFile.parentFile?.mkdirs()
        outFile.outputStream().buffered().use { GifEncoder().encode(frames, GIF_DELAY_CENTISECONDS, it) }

        if (outFile.length() > MAX_GIF_SIZE_BYTES) {
            // Re-encode keeping every other frame to reduce file size
            val halfFrames = frames.filterIndexed { i, _ -> i % 2 == 0 }
            outFile.delete()
            outFile.outputStream().buffered().use {
                GifEncoder().encode(halfFrames, GIF_DELAY_CENTISECONDS * 2, it)
            }
        }

        frames.forEach { it.recycle() }
        outFile
    }

    private fun buildPhotoMatrix(slot: RectF, photo: Bitmap, transform: SlotTransform): Matrix {
        val matrix = Matrix()
        val photoAspect = photo.width.toFloat() / photo.height
        val slotAspect = slot.width() / slot.height()
        val baseScale = if (photoAspect > slotAspect)
            slot.height() / photo.height else slot.width() / photo.width
        val scale = baseScale * transform.scale
        val cx = photo.width / 2f
        val cy = photo.height / 2f
        if (transform.flipHorizontal) matrix.preScale(-1f, 1f, cx, cy)
        matrix.postScale(scale, scale)
        matrix.postTranslate(
            slot.left + (slot.width() - photo.width * scale) / 2f + transform.panOffset.x,
            slot.top + (slot.height() - photo.height * scale) / 2f + transform.panOffset.y,
        )
        return matrix
    }

    private fun sessionFile(sessionId: String, name: String) =
        File(context.cacheDir, "session_$sessionId/$name")
}

data class SlotTransform(
    val panOffset: android.graphics.PointF = android.graphics.PointF(0f, 0f),
    val scale: Float = 1f,
    val flipHorizontal: Boolean = false,
)

fun Layout.canvasHeight(): Int = when {
    slotCount <= 4 -> slotCount * 288
    else -> ((slotCount + 1) / 2) * 256
}

fun Layout.slotRects(canvasWidth: Int, canvasHeight: Int): List<RectF> {
    val cols = if (slotCount <= 4) 1 else 2
    val rows = if (cols == 1) slotCount else (slotCount + 1) / 2
    val slotW = canvasWidth.toFloat() / cols
    val slotH = canvasHeight.toFloat() / rows
    return (0 until slotCount).map { i ->
        val col = i % cols
        val row = i / cols
        RectF(col * slotW, row * slotH, (col + 1) * slotW, (row + 1) * slotH)
    }
}
