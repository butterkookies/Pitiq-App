package com.pitiq.app.kiosk

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class KioskViewModel @Inject constructor(
    kioskController: KioskController,
) : ViewModel() {
    val isConfigured: StateFlow<Boolean> = kioskController.isConfigured
}
