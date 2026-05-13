package com.pitiq.app.hardware.media

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

@RunWith(AndroidJUnit4::class)
class GifEncoderTest {

    private val encoder = GifEncoder()

    private fun solidBitmap(width: Int, height: Int, color: Int): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { it.eraseColor(color) }

    @Test
    fun `ten-frame burst at 480x640 is under 5MB`() {
        val frames = List(10) { solidBitmap(480, 640, Color.rgb(200, 150, 100)) }
        val out = ByteArrayOutputStream()
        assertTrue(encoder.encode(frames, delayCentiseconds = 10, out = out))
        val kb = out.size() / 1024
        assertTrue("GIF is ${kb}KB — exceeds 5MB limit", out.size() < 5 * 1024 * 1024)
    }

    @Test
    fun `diverse color burst is under 5MB`() {
        val colors = listOf(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.CYAN, Color.MAGENTA, Color.WHITE, Color.BLACK,
            Color.GRAY, Color.DKGRAY,
        )
        val frames = colors.map { solidBitmap(480, 640, it) }
        val out = ByteArrayOutputStream()
        encoder.encode(frames, delayCentiseconds = 10, out = out)
        assertTrue(out.size() < 5 * 1024 * 1024)
    }

    @Test
    fun `output starts with GIF89a magic bytes`() {
        val frames = listOf(solidBitmap(100, 100, Color.RED))
        val out = ByteArrayOutputStream()
        encoder.encode(frames, delayCentiseconds = 10, out = out)
        val header = out.toByteArray().take(6).toByteArray().toString(Charsets.US_ASCII)
        assertEquals("GIF89a", header)
    }

    @Test
    fun `output ends with GIF trailer byte 0x3B`() {
        val frames = listOf(solidBitmap(50, 50, Color.BLUE))
        val out = ByteArrayOutputStream()
        encoder.encode(frames, delayCentiseconds = 10, out = out)
        val last = out.toByteArray().last().toInt() and 0xFF
        assertEquals(0x3B, last)
    }

    @Test
    fun `empty frame list returns false and writes nothing`() {
        val out = ByteArrayOutputStream()
        assertFalse(encoder.encode(emptyList(), delayCentiseconds = 10, out = out))
        assertEquals(0, out.size())
    }

    @Test
    fun `single frame encodes successfully`() {
        val out = ByteArrayOutputStream()
        assertTrue(encoder.encode(listOf(solidBitmap(100, 100, Color.GREEN)), 10, out))
        assertTrue(out.size() > 6)
    }
}
