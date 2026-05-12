package com.pitiq.app.ui.navigation

sealed class Screen(val route: String) {
    data object Attract : Screen("attract")
    data object OperatorSetup : Screen("operator_setup")
    data object Payment : Screen("payment")
    data object LayoutSelection : Screen("layout_selection")
    data object PhotoCapture : Screen("photo_capture")
    data object Edit : Screen("edit")
    data object Print : Screen("print")
    data object Upload : Screen("upload")
    data object QRShare : Screen("qr_share")
}
