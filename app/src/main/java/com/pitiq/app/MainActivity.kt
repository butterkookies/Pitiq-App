package com.pitiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pitiq.app.kiosk.KioskController
import com.pitiq.app.kiosk.KioskManager
import com.pitiq.app.ui.navigation.AppNavigation
import com.pitiq.app.ui.theme.PitiqTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var kioskController: KioskController
    @Inject lateinit var kioskManager: KioskManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        applyImmersiveMode()
        kioskManager.configureKioskPolicies()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                kioskController.shouldLock.collect { lock ->
                    if (lock) kioskManager.startLockTask(this@MainActivity)
                    else kioskManager.stopLockTask(this@MainActivity)
                }
            }
        }

        setContent {
            PitiqTheme {
                AppNavigation()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyImmersiveMode()
    }

    private fun applyImmersiveMode() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Back navigation suppressed during kiosk session.
        // Operator exit is handled via PIN-protected flow (Phase 1.1.5).
    }
}
