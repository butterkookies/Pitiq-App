package com.pitiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pitiq.app.data.repository.LayoutSyncManager
import com.pitiq.app.data.update.UpdateChecker
import com.pitiq.app.data.update.UpdateState
import com.pitiq.app.domain.state.SessionState
import com.pitiq.app.kiosk.KioskController
import com.pitiq.app.kiosk.KioskManager
import com.pitiq.app.session.SessionViewModel
import com.pitiq.app.ui.navigation.AppNavigation
import com.pitiq.app.ui.screen.update.UpdateAvailableDialog
import com.pitiq.app.ui.theme.PitiqTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var kioskController: KioskController
    @Inject lateinit var kioskManager: KioskManager
    @Inject lateinit var layoutSyncManager: LayoutSyncManager
    @Inject lateinit var updateChecker: UpdateChecker

    private val sessionViewModel: SessionViewModel by viewModels()

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

        // App launch: sync layouts and check for available update
        lifecycleScope.launch {
            layoutSyncManager.sync()
            updateChecker.check()
        }

        setContent {
            PitiqTheme {
                Box(Modifier.fillMaxSize()) {
                    AppNavigation()

                    val updateState by updateChecker.state.collectAsState()
                    val sessionState by sessionViewModel.sessionState.collectAsState()

                    // Show update dialog only when no active customer session
                    if (updateState !is UpdateState.None && sessionState is SessionState.Idle) {
                        UpdateAvailableDialog(
                            state = updateState,
                            onDownloadAndInstall = { info ->
                                lifecycleScope.launch { updateChecker.downloadAndInstall(info) }
                            },
                            onDismiss = updateChecker::dismiss,
                        )
                    }
                }
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
