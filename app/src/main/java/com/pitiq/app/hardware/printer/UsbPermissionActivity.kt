package com.pitiq.app.hardware.printer

import android.app.Activity
import android.os.Bundle

// TODO Phase 2.2: handle USB device attached broadcast, request UsbManager permission
class UsbPermissionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
