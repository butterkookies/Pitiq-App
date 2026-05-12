package com.pitiq.app.hardware.media

import android.graphics.Bitmap
import java.io.OutputStream

/**
 * Encodes a list of Bitmaps as an animated GIF89a file.
 * Uses a fixed 6×6×6 color cube (216 entries) + 40 grayscale steps = 256-color palette
 * with LZW compression per GIF spec.
 */
class GifEncoder {

    fun encode(frames: List<Bitmap>, delayCentiseconds: Int, out: OutputStream): Boolean {
        if (frames.isEmpty()) return false
        val width = frames[0].width
        val height = frames[0].height

        writeHeader(out, width, height)
        writeNetscapeExtension(out, 0) // 0 = loop forever

        for (frame in frames) {
            val (palette, indexed) = quantize(frame)
            writeGraphicControlExtension(out, delayCentiseconds)
            writeImageDescriptor(out, width, height)
            writeColorTable(out, palette)
            writeLzwData(out, indexed)
        }

        out.write(0x3B) // GIF trailer
        return true
    }

    // ---- GIF structural blocks ----

    private fun writeHeader(out: OutputStream, width: Int, height: Int) {
        out.write("GIF89a".toByteArray(Charsets.US_ASCII))
        writeShort(out, width)
        writeShort(out, height)
        // Packed: no global color table (0x70 = color resolution 7, no GCT)
        out.write(0x70)
        out.write(0) // background color index
        out.write(0) // pixel aspect ratio
    }

    private fun writeNetscapeExtension(out: OutputStream, loops: Int) {
        out.write(0x21); out.write(0xFF)
        out.write(11)
        out.write("NETSCAPE2.0".toByteArray(Charsets.US_ASCII))
        out.write(3); out.write(1)
        writeShort(out, loops)
        out.write(0)
    }

    private fun writeGraphicControlExtension(out: OutputStream, delay: Int) {
        out.write(0x21); out.write(0xF9)
        out.write(4)
        out.write(0x04) // packed: disposal=1, no user input, no transparency
        writeShort(out, delay)
        out.write(0) // transparent color index (unused)
        out.write(0) // block terminator
    }

    private fun writeImageDescriptor(out: OutputStream, width: Int, height: Int) {
        out.write(0x2C)
        writeShort(out, 0); writeShort(out, 0) // left, top
        writeShort(out, width); writeShort(out, height)
        // Packed: local color table flag=1, no interlace, lct size=7 (2^8=256 entries)
        out.write(0x87)
    }

    private fun writeColorTable(out: OutputStream, palette: IntArray) {
        for (color in palette) {
            out.write((color shr 16) and 0xFF)
            out.write((color shr 8) and 0xFF)
            out.write(color and 0xFF)
        }
        // Pad to exactly 256 entries
        repeat(256 - palette.size) { out.write(0); out.write(0); out.write(0) }
    }

    private fun writeLzwData(out: OutputStream, indexed: IntArray) {
        val minCodeSize = 8
        out.write(minCodeSize)
        LzwEncoder(minCodeSize).encode(indexed, out)
        out.write(0) // block terminator
    }

    private fun writeShort(out: OutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
    }

    // ---- Color quantization ----

    private fun quantize(bitmap: Bitmap): Pair<IntArray, IntArray> {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val palette = buildPalette256()
        val indexed = IntArray(pixels.size) { nearestIndex(pixels[it], palette) }
        return Pair(palette, indexed)
    }

    private fun buildPalette256(): IntArray {
        val palette = IntArray(256)
        var i = 0
        // 6×6×6 color cube (216 entries)
        for (r in 0..5) for (g in 0..5) for (b in 0..5) {
            palette[i++] = ((r * 51) shl 16) or ((g * 51) shl 8) or (b * 51)
        }
        // 40 evenly-spaced grayscale entries to fill up to 256
        for (k in 0 until 40) {
            val gray = k * 255 / 39
            palette[i++] = (gray shl 16) or (gray shl 8) or gray
        }
        return palette
    }

    private fun nearestIndex(argb: Int, palette: IntArray): Int {
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        var best = 0
        var bestDist = Int.MAX_VALUE
        for (i in palette.indices) {
            val pr = (palette[i] shr 16) and 0xFF
            val pg = (palette[i] shr 8) and 0xFF
            val pb = palette[i] and 0xFF
            val dr = r - pr; val dg = g - pg; val db = b - pb
            val dist = dr * dr + dg * dg + db * db
            if (dist < bestDist) { bestDist = dist; best = i }
        }
        return best
    }
}

// ---- LZW encoder ----

private class LzwEncoder(private val minCodeSize: Int) {

    fun encode(indices: IntArray, out: OutputStream) {
        val clearCode = 1 shl minCodeSize
        val eoiCode = clearCode + 1
        var codeSize = minCodeSize + 1
        var nextCode = eoiCode + 1

        val table = HashMap<Int, Int>(5003)
        val resetTable: () -> Unit = {
            table.clear()
            codeSize = minCodeSize + 1
            nextCode = eoiCode + 1
        }

        val bits = BitAccumulator(out)
        resetTable()
        bits.write(clearCode, codeSize)

        if (indices.isEmpty()) {
            bits.write(eoiCode, codeSize)
            bits.flush()
            return
        }

        var prefix = indices[0]
        for (i in 1 until indices.size) {
            val k = indices[i]
            val key = (prefix shl 8) or k
            val found = table[key]
            if (found != null) {
                prefix = found
            } else {
                bits.write(prefix, codeSize)
                if (nextCode <= 4095) {
                    table[key] = nextCode++
                    if (nextCode >= (1 shl codeSize) && codeSize < 12) codeSize++
                } else {
                    bits.write(clearCode, codeSize)
                    resetTable()
                }
                prefix = k
            }
        }
        bits.write(prefix, codeSize)
        bits.write(eoiCode, codeSize)
        bits.flush()
    }
}

private class BitAccumulator(private val out: OutputStream) {
    private var accum = 0
    private var bits = 0
    private val block = ByteArray(256)
    private var blockLen = 0

    fun write(code: Int, codeSize: Int) {
        accum = accum or (code shl bits)
        bits += codeSize
        while (bits >= 8) {
            block[blockLen++] = (accum and 0xFF).toByte()
            accum = accum ushr 8
            bits -= 8
            if (blockLen >= 254) flushBlock()
        }
    }

    fun flush() {
        if (bits > 0) block[blockLen++] = (accum and 0xFF).toByte()
        if (blockLen > 0) flushBlock()
    }

    private fun flushBlock() {
        out.write(blockLen)
        out.write(block, 0, blockLen)
        blockLen = 0
    }
}
