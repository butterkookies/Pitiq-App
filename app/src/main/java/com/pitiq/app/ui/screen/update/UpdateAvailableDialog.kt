package com.pitiq.app.ui.screen.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pitiq.app.data.remote.UpdateInfo
import com.pitiq.app.data.update.UpdateState

@Composable
fun UpdateAvailableDialog(
    state: UpdateState,
    onDownloadAndInstall: (UpdateInfo) -> Unit,
    onDismiss: () -> Unit,
) {
    val info = when (state) {
        is UpdateState.Available        -> state.info
        is UpdateState.Downloading      -> null
        is UpdateState.ReadyToInstall   -> state.info
        UpdateState.None                -> return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Update Available — v${info?.versionName ?: ""}")
        },
        text = {
            Column {
                if (info != null && info.changelog.isNotBlank()) {
                    Text(
                        text = info.changelog,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (state is UpdateState.Downloading) {
                    CircularProgressIndicator(Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { info?.let(onDownloadAndInstall) },
                enabled = state is UpdateState.Available,
            ) {
                Text(if (state is UpdateState.Downloading) "Downloading…" else "Download & Install")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later")
            }
        },
    )
}
