package com.pitiq.app.ui.screen.setup

import androidx.lifecycle.ViewModel
import com.pitiq.app.data.local.prefs.SecurePreferences
import com.pitiq.app.kiosk.KioskController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OperatorSetupViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val kioskController: KioskController,
) : ViewModel() {

    fun save(locationId: String, pin: String) {
        securePreferences.locationId = locationId
        securePreferences.operatorPin = pin
        kioskController.markConfigured()
    }

    companion object {
        val LOCATION_ID_REGEX = Regex("^[A-Za-z0-9_-]{3,32}$")
        val PIN_REGEX = Regex("^\\d{4,6}$")
    }
}
