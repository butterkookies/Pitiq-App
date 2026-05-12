package com.pitiq.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pitiq.app.domain.state.SessionState
import com.pitiq.app.kiosk.KioskViewModel
import com.pitiq.app.session.SessionViewModel
import com.pitiq.app.ui.screen.attract.AttractScreen
import com.pitiq.app.ui.screen.capture.PhotoCaptureScreen
import com.pitiq.app.ui.screen.edit.EditScreen
import com.pitiq.app.ui.screen.layout.LayoutSelectionScreen
import com.pitiq.app.ui.screen.payment.PaymentScreen
import com.pitiq.app.ui.screen.print.PrintScreen
import com.pitiq.app.ui.screen.qrshare.QRShareScreen
import com.pitiq.app.ui.screen.setup.OperatorSetupScreen
import com.pitiq.app.ui.screen.upload.UploadScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = hiltViewModel(),
    kioskViewModel: KioskViewModel = hiltViewModel(),
) {
    val isConfigured by kioskViewModel.isConfigured.collectAsState()

    if (!isConfigured) {
        OperatorSetupScreen()
        return
    }

    val sessionState by sessionViewModel.sessionState.collectAsState()

    // Drive navigation from SessionState so screens never navigate independently.
    LaunchedEffect(sessionState) {
        when (sessionState) {
            is SessionState.Idle -> navController.navigate(Screen.Attract.route) {
                popUpTo(0) { inclusive = true }
            }
            is SessionState.Payment -> navController.navigate(Screen.Payment.route)
            is SessionState.LayoutSelection -> navController.navigate(Screen.LayoutSelection.route)
            is SessionState.PhotoCapture -> navController.navigate(Screen.PhotoCapture.route)
            is SessionState.Edit -> navController.navigate(Screen.Edit.route)
            is SessionState.Print -> navController.navigate(Screen.Print.route)
            is SessionState.Upload -> navController.navigate(Screen.Upload.route)
            is SessionState.QRShare -> navController.navigate(Screen.QRShare.route)
        }
    }

    NavHost(navController = navController, startDestination = Screen.Attract.route) {
        composable(Screen.Attract.route) {
            AttractScreen(
                onTap = { sessionViewModel.initSession() },
            )
        }
        composable(Screen.Payment.route) {
            PaymentScreen(
                onCancel = { sessionViewModel.cancelSession() },
            )
        }
        composable(Screen.LayoutSelection.route) {
            LayoutSelectionScreen(
                onLayoutConfirmed = { layout -> sessionViewModel.onLayoutSelected(layout) },
            )
        }
        composable(Screen.PhotoCapture.route) {
            val state = sessionState as? SessionState.PhotoCapture
            PhotoCaptureScreen(
                isRetake = state?.isRetake == true,
                onSlotCaptured = { photo -> sessionViewModel.onSlotCaptured(photo) },
                onRetakeComplete = { photo -> sessionViewModel.onRetakeComplete(photo) },
            )
        }
        composable(Screen.Edit.route) {
            EditScreen(
                onPrintRequested = { sessionViewModel.onPrintRequested() },
                onRetakeRequested = { slot -> sessionViewModel.onRetakeRequested(slot) },
            )
        }
        composable(Screen.Print.route) {
            PrintScreen(
                onPrintSuccess = { sessionViewModel.onPrintSuccess() },
                onPrintFailed = { error -> sessionViewModel.onPrintFailed(error) },
            )
        }
        composable(Screen.Upload.route) {
            UploadScreen(
                onUploadComplete = { url -> sessionViewModel.onUploadComplete(url) },
                onUploadFailed = { error -> sessionViewModel.onUploadFailed(error) },
            )
        }
        composable(Screen.QRShare.route) {
            val state = sessionState as? SessionState.QRShare
            QRShareScreen(
                shareUrl = state?.shareUrl.orEmpty(),
                onExpired = { sessionViewModel.resetToAttract() },
            )
        }
    }
}
