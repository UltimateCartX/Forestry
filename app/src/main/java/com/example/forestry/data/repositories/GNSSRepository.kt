package com.example.forestry.data.repositories

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.forestry.data.models.BluetoothConnectionState
import com.example.forestry.ui.previews.GNSSRepositoryLike
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import java.util.UUID

/**
 * Handles communication with the GNSS receptor
 */
class GNSSRepository(private val context: Context) : GNSSRepositoryLike {

    private val _connectionState = MutableStateFlow<BluetoothConnectionState>(BluetoothConnectionState.Disconnected)
    override val connectionState: StateFlow<BluetoothConnectionState> = _connectionState

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

    override fun getBondedDevices(): List<BluetoothDevice> {
        Log.d("Forestry", "Overrided")
        return if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.bondedDevices.toList()
        } else {
            emptyList()
        }
    }

    private val _incomingData = MutableStateFlow<String?>(null)
    override val incomingData: StateFlow<String?> = _incomingData

    private var socket: BluetoothSocket? = null

    private fun getDeviceUuid(device: BluetoothDevice): UUID? {
        return if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            device.uuids?.firstOrNull()?.uuid
            } else {
                null
            }
    }

    override fun connect(device: BluetoothDevice) {
        _connectionState.value = BluetoothConnectionState.Connecting
        val uuid = getDeviceUuid(device)
        socket = device.createRfcommSocketToServiceRecord(uuid)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket?.connect()
                _connectionState.value = BluetoothConnectionState.Connected
                listenForData()
            } catch (e: IOException) {
                e.printStackTrace()
                _connectionState.value = BluetoothConnectionState.Error("Connection Failed")
            }
        }
    }

    private suspend fun listenForData() {
        val inputStream = socket?.inputStream ?: return
        val buffer = ByteArray(1024)

        try {
            while (true) {
                val bytes = inputStream.read(buffer)

                if (bytes == -1) {
                    throw IOException("Stream closed")
                }

                if (bytes > 0) {
                    val message = String(buffer, 0, bytes)

                    withContext(Dispatchers.Main) {
                        _incomingData.value = message
                    }
                }
            }
        } catch (e: IOException) {
            _connectionState.value = BluetoothConnectionState.Disconnected
        }
    }

    override fun disconnect() {
        socket?.close()
        _connectionState.value = BluetoothConnectionState.Disconnected
    }
}