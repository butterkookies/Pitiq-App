package com.pitiq.app.ui.screen.payment

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pitiq.app.BuildConfig
import com.pitiq.app.hardware.bluetooth.BluetoothState

private const val TARGET_AMOUNT = 40

@Composable
fun PaymentScreen(
    onPaymentComplete: () -> Unit,
    onCoinInserted: (Int) -> Unit,
    onCancel: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val coinTotal by viewModel.coinTotal.collectAsState()
    val btState by viewModel.bluetoothState.collectAsState()
    val timeoutCancelled by viewModel.timeoutCancelled.collectAsState()

    // Forward coin total updates to SessionViewModel
    LaunchedEffect(coinTotal) { onCoinInserted(coinTotal) }

    // Auto-navigate when payment is complete
    LaunchedEffect(coinTotal) {
        if (coinTotal >= TARGET_AMOUNT) onPaymentComplete()
    }

    // Timeout/BT failure → cancel session
    LaunchedEffect(timeoutCancelled) {
        if (timeoutCancelled) onCancel()
    }

    val progress by animateFloatAsState(
        targetValue = (coinTotal.toFloat() / TARGET_AMOUNT).coerceAtMost(1f),
        animationSpec = tween(400),
        label = "coinProgress",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // No-change notice
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2000))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "No change will be given",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = "₱$coinTotal",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "of ₱$TARGET_AMOUNT",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 22.sp,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Coin step indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                listOf(10, 20, 30, 40).forEach { step ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (coinTotal >= step) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                ),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₱$step",
                            color = if (coinTotal >= step) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Insert coins to continue",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 18.sp,
            )

            if (BuildConfig.DEBUG) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(10, 20, 40).forEach { amount ->
                        Button(
                            onClick = { viewModel.simulateCoin(amount) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A00),
                                contentColor = MaterialTheme.colorScheme.secondary,
                            ),
                        ) {
                            Text(text = "+₱$amount", fontSize = 13.sp)
                        }
                    }
                }
                Text(
                    text = "debug — simulated coins",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        // Bluetooth reconnecting overlay
        if (btState == BluetoothState.Disconnected || btState == BluetoothState.Connecting ||
            btState == BluetoothState.HandshakePending
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB3000000)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Reconnecting…",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 22.sp,
                )
            }
        }
    }
}
