package com.pitiq.app.ui.screen.attract

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * PIN-entry dialog shown when the operator long-presses the hidden exit trigger.
 * [onExit] receives the entered PIN and returns true if it matched (dialog should close)
 * or false if it was wrong (dialog stays open with error shown).
 */
@Composable
fun OperatorExitDialog(
    onDismiss: () -> Unit,
    onExit: (pin: String) -> Boolean,
) {
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    fun attempt() {
        if (!onExit(pin)) showError = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Operator Exit") },
        text = {
            Column {
                Text("Enter your PIN to exit kiosk mode.")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) {
                            pin = it.take(6)
                            showError = false
                        }
                    },
                    label = { Text("Operator PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { attempt() }),
                    isError = showError,
                    supportingText = if (showError) ({ Text("Incorrect PIN") }) else null,
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { attempt() },
                enabled = pin.length >= 4,
            ) { Text("Exit Kiosk") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
