package com.example.forestry.data.models

sealed class BluetoothConnectionState {
    data object Connecting: BluetoothConnectionState()
    data object Connected: BluetoothConnectionState()
    data class Error(val message: String): BluetoothConnectionState()
    data object Disconnected: BluetoothConnectionState()
}