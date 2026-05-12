package com.pitiq.app.kiosk

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KioskManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dpm: DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private val adminComponent = ComponentName(context, KioskDeviceAdminReceiver::class.java)

    val isDeviceOwner: Boolean
        get() = runCatching { dpm.isDeviceOwnerApp(context.packageName) }.getOrDefault(false)

    /**
     * Whitelists this package for lock task, disables status bar and keyguard.
     * No-ops silently if the app is not provisioned as Device Owner.
     */
    fun configureKioskPolicies(): Boolean {
        if (!isDeviceOwner) return false
        return runCatching {
            dpm.setLockTaskPackages(adminComponent, arrayOf(context.packageName))
            dpm.setStatusBarDisabled(adminComponent, true)
            dpm.setKeyguardDisabled(adminComponent, true)
            true
        }.getOrDefault(false)
    }

    fun startLockTask(activity: Activity) {
        runCatching { activity.startLockTask() }
    }

    fun stopLockTask(activity: Activity) {
        runCatching { activity.stopLockTask() }
    }
}
