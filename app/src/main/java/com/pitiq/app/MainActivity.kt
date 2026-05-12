package com.pitiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pitiq.app.ui.navigation.AppNavigation
import com.pitiq.app.ui.theme.PitiqTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PitiqTheme {
                AppNavigation()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Back navigation suppressed during kiosk session.
        // Operator exit is handled via PIN-protected flow (Phase 1).
    }
}
