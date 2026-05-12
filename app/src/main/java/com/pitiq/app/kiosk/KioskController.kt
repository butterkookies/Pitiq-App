package com.pitiq.app.kiosk

import com.pitiq.app.data.local.prefs.SecurePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KioskController @Inject constructor(
    securePreferences: SecurePreferences,
) {
    private val _isConfigured = MutableStateFlow(securePreferences.isConfigured)
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    private val _shouldLock = MutableStateFlow(securePreferences.isConfigured)
    val shouldLock: StateFlow<Boolean> = _shouldLock.asStateFlow()

    fun markConfigured() {
        _isConfigured.value = true
        _shouldLock.value = true
    }

    /** Called by the operator exit flow (Phase 1.1.5) to release lock task. */
    fun requestExit() {
        _shouldLock.value = false
    }
}
