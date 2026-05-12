package com.pitiq.app.hardware.printer

sealed class PrintResult {
    data object Success : PrintResult()
    data object PaperOut : PrintResult()
    data object PaperJam : PrintResult()
    data object PrinterDisconnect : PrintResult()
    data object Timeout : PrintResult()

    val errorMessage: String
        get() = when (this) {
            is Success -> ""
            is PaperOut -> "Printer error: paper out"
            is PaperJam -> "Printer error: paper jam"
            is PrinterDisconnect -> "Printer not connected"
            is Timeout -> "Printer timeout — no response within 15 seconds"
        }
}
