package com.example.forestry.ui.composables

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun DeviceItem(
    device: BluetoothDevice,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val name = device.name ?: "Unknown Device"

    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = name, modifier = Modifier.padding(vertical = 8.dp))
    }
}