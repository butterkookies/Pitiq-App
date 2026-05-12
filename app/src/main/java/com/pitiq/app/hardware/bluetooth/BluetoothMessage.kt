package com.pitiq.app.hardware.bluetooth

sealed class BluetoothMessage {
    /** A coin was accepted by the CH-926; amountPeso is ₱1, ₱5, or ₱10. */
    data class CoinInserted(val amountPeso: Int) : BluetoothMessage()

    /** ESP32 confirmed a graceful disconnect. */
    data object DisconnectAck : BluetoothMessage()

    /** ESP32 responded with buffered coin total accumulated while disconnected. */
    data class CoinBufferResponse(val totalPeso: Int) : BluetoothMessage()
}
