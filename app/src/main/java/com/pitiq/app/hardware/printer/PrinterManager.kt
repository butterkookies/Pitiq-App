package com.pitiq.app.hardware.printer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the USB OTG connection to the 58mm thermal printer.
 *
 * ESC/POS commands used:
 *   ESC @        — initialize printer
 *   GS v 0       — print raster bit image (1 bit/pixel, MSB first)
 *   ESC d n      — feed n lines
 *   GS V 0       — full cut
 *
 * IMPORTANT: VID/PID below are placeholders. Confirm against the physical
 * printer with `adb shell lsusb` or UsbManager.getDeviceList() before deployment.
 */
@Singleton
class PrinterManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val ACTION_USB_PERMISSION = "com.pitiq.app.USB_PERMISSION"

        // Placeholder VID/PID — must be confirmed against physical printer.
        private const val THERMAL_VID = 0x0483
        private const val THERMAL_PID = 0x5740

        private const val PRINTER_WIDTH_PIXELS = 384   // 58mm at 203 DPI
        private const val USB_PACKET_SIZE = 64
        private const val PRINT_TIMEOUT_MS = 15_000L
    }

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    /** True only if the hardware supports USB Host (not all phones do). */
    val isUsbHostSupported: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)

    /** Find the printer in the USB device list by VID/PID. Returns null if not attached. */
    fun findPrinterDevice(): UsbDevice? =
        usbManager.deviceList.values.firstOrNull { it.vendorId == THERMAL_VID && it.productId == THERMAL_PID }

    /**
     * Returns true if the printer is attached and USB permission has been granted.
     * Does NOT open a connection — safe to poll every 10 seconds from the attract screen.
     */
    fun isPrinterConnected(): Boolean {
        if (!isUsbHostSupported) return false
        val device = findPrinterDevice() ?: return false
        return usbManager.hasPermission(device)
    }

    /**
     * Request USB permission for the printer device. Result delivered via [onResult].
     * No-op if device is not attached or permission is already granted.
     */
    fun requestPermission(onResult: (Boolean) -> Unit) {
        val device = findPrinterDevice() ?: return onResult(false)
        if (usbManager.hasPermission(device)) return onResult(true)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                context.unregisterReceiver(this)
                onResult(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
            }
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_MUTABLE else 0
        val permissionIntent = PendingIntent.getBroadcast(
            context, 0, Intent(ACTION_USB_PERMISSION), flags
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                receiver, IntentFilter(ACTION_USB_PERMISSION), Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, IntentFilter(ACTION_USB_PERMISSION))
        }
        usbManager.requestPermission(device, permissionIntent)
    }

    /** Send [bitmap] to the printer via ESC/POS over USB bulk transfer. */
    suspend fun print(bitmap: Bitmap): PrintResult = withContext(Dispatchers.IO) {
        val device = findPrinterDevice() ?: return@withContext PrintResult.PrinterDisconnect
        if (!usbManager.hasPermission(device)) return@withContext PrintResult.PrinterDisconnect

        val connection = usbManager.openDevice(device)
            ?: return@withContext PrintResult.PrinterDisconnect

        try {
            val iface = device.getInterface(0)
            connection.claimInterface(iface, true)

            val outEndpoint = (0 until iface.endpointCount)
                .map { iface.getEndpoint(it) }
                .firstOrNull { it.direction == UsbConstants.USB_DIR_OUT }
                ?: return@withContext PrintResult.PrinterDisconnect

            val data = buildPrintData(bitmap)

            withTimeout(PRINT_TIMEOUT_MS) {
                var offset = 0
                while (offset < data.size) {
                    val chunk = minOf(USB_PACKET_SIZE, data.size - offset)
                    val sent = connection.bulkTransfer(outEndpoint, data, offset, chunk, 5_000)
                    if (sent < 0) return@withTimeout
                    offset += sent
                }
            }
            PrintResult.Success
        } catch (_: TimeoutCancellationException) {
            PrintResult.Timeout
        } catch (_: Exception) {
            PrintResult.PrinterDisconnect
        } finally {
            connection.close()
        }
    }

    private fun buildPrintData(source: Bitmap): ByteArray {
        val scaled = scaleTo(source, PRINTER_WIDTH_PIXELS)
        val widthBytes = (scaled.width + 7) / 8
        val height = scaled.height

        val out = ByteArrayOutputStream()

        // ESC @ — initialize printer
        out.write(byteArrayOf(0x1B, 0x40))

        // GS v 0 m=0 — raster bit image at normal density
        out.write(byteArrayOf(
            0x1D, 0x76, 0x30, 0x00,
            (widthBytes and 0xFF).toByte(),
            ((widthBytes shr 8) and 0xFF).toByte(),
            (height and 0xFF).toByte(),
            ((height shr 8) and 0xFF).toByte(),
        ))

        // Pixel data: 1 bit per pixel, MSB first; dark pixel = 1
        for (y in 0 until height) {
            for (xByte in 0 until widthBytes) {
                var byte = 0
                for (bit in 0 until 8) {
                    val x = xByte * 8 + bit
                    if (x < scaled.width) {
                        val pixel = scaled.getPixel(x, y)
                        val luma = (Color.red(pixel) * 0.299
                                + Color.green(pixel) * 0.587
                                + Color.blue(pixel) * 0.114).toInt()
                        if (luma < 128) byte = byte or (0x80 shr bit)
                    }
                }
                out.write(byte)
            }
        }

        // ESC d 4 — feed 4 lines; GS V 0 — full cut
        out.write(byteArrayOf(0x1B, 0x64, 0x04, 0x1D, 0x56, 0x00))

        return out.toByteArray()
    }

    private fun scaleTo(source: Bitmap, targetWidth: Int): Bitmap {
        val targetHeight = (source.height * targetWidth.toFloat() / source.width).toInt()
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }
}
