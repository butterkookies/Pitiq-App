package com.pitiq.app.ui.screen.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OperatorSetupScreen(
    viewModel: OperatorSetupViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current

    var locationId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    val locationIdValid = OperatorSetupViewModel.LOCATION_ID_REGEX.matches(locationId)
    val pinValid = OperatorSetupViewModel.PIN_REGEX.matches(pin)
    val pinsMatch = pin == confirmPin && confirmPin.isNotEmpty()
    val canSave = locationIdValid && pinValid && pinsMatch

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Operator Setup",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "One-time configuration. PIN required to exit kiosk.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = locationId,
                onValueChange = { locationId = it.take(32) },
                label = { Text("Location ID") },
                isError = locationId.isNotEmpty() && !locationIdValid,
                supportingText = if (locationId.isNotEmpty() && !locationIdValid) {
                    { Text("3–32 letters, digits, _ or -") }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.all { c -> c.isDigit() }) pin = it.take(6) },
                label = { Text("Operator PIN (4–6 digits)") },
                isError = pin.isNotEmpty() && !pinValid,
                supportingText = if (pin.isNotEmpty() && !pinValid) {
                    { Text("Enter 4–6 digits") }
                } else null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = confirmPin,
                onValueChange = { if (it.all { c -> c.isDigit() }) confirmPin = it.take(6) },
                label = { Text("Confirm PIN") },
                isError = confirmPin.isNotEmpty() && confirmPin != pin,
                supportingText = if (confirmPin.isNotEmpty() && confirmPin != pin) {
                    { Text("PINs don't match") }
                } else null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (canSave) viewModel.save(locationId, pin)
                    },
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.save(locationId, pin) },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text("Activate Kiosk", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
