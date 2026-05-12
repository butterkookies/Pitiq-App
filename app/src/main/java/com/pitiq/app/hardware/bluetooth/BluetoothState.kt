package com.pitiq.app.hardware.bluetooth

sealed class BluetoothState {
    data object Disconnected : BluetoothState()
    data object Connecting : BluetoothState()
    data object HandshakePending : BluetoothState()
    data object Connected : BluetoothState()
    data object ReconnectFailed : BluetoothState()
}
