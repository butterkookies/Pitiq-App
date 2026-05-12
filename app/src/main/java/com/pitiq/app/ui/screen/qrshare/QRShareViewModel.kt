package com.pitiq.app.ui.screen.qrshare

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QRShareViewModel @Inject constructor() : ViewModel() {

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()

    private val _timeRemaining = MutableStateFlow(60)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    fun init(shareUrl: String) {
        generateQr(shareUrl)
        startCountdown()
    }

    private fun generateQr(url: String) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                val hints = mapOf(EncodeHintType.MARGIN to 1)
                val matrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 512, 512, hints)
                val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
                for (x in 0 until 512) for (y in 0 until 512) {
                    bmp.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
                bmp
            }.onSuccess { _qrBitmap.value = it }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (isActive && _timeRemaining.value > 0) {
                delay(1_000)
                _timeRemaining.value = (_timeRemaining.value - 1).coerceAtLeast(0)
            }
        }
    }
}
