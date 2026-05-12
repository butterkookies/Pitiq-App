package com.pitiq.app.kiosk

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

// TODO Phase 1.1: implement lock task, UI suppression via DevicePolicyManager
class KioskDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) = Unit

    override fun onDisabled(context: Context, intent: Intent) = Unit
}
