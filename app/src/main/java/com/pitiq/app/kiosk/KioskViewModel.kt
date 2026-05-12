package com.pitiq.app.kiosk

import androidx.lifecycle.ViewModel
import com.pitiq.app.data.local.prefs.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class KioskViewModel @Inject constructor(
    private val kioskController: KioskController,
    private val securePreferences: SecurePreferences,
) : ViewModel() {

    val isConfigured: StateFlow<Boolean> = kioskController.isConfigured

    /**
     * Verifies [pin] against the stored operator PIN.
     * On match: signals [KioskController] to release lock task and returns true.
     * On mismatch: returns false so the caller can show an error.
     */
    fun verifyPinAndExit(pin: String): Boolean {
        val stored = securePreferences.operatorPin ?: return false
        return if (pin == stored) {
            kioskController.requestExit()
            true
        } else {
            false
        }
    }
}
