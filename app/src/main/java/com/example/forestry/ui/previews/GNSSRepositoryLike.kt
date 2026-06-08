package com.example.forestry.ui.previews

import android.bluetooth.BluetoothDevice
import com.example.forestry.data.models.BluetoothConnectionState
import kotlinx.coroutines.flow.StateFlow

interface GNSSRepositoryLike {
    val connectionState: StateFlow<BluetoothConnectionState>
    val incomingData: StateFlow<String?>

    fun getBondedDevices(): List<BluetoothDevice>
    fun connect(device: BluetoothDevice)
    fun disconnect()
}