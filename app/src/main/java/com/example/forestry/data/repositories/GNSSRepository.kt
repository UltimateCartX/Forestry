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
import com.example.forestry.data.enums.BluetoothConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class GNSSRepository(context: Context) {
    private val applicationContext = context.applicationContext

    companion object {
        private const val TAG = "GNSSRepository"
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val _connectionState = MutableStateFlow(BluetoothConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _incomingData = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val incomingData = _incomingData.asSharedFlow()

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter
    }

    private var socket: BluetoothSocket? = null
    private val connectionMutex = Mutex()

    fun getBondedDevices(): List<BluetoothDevice> {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) return emptyList()
        
        return bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }

    suspend fun connect(device: BluetoothDevice) = withContext(Dispatchers.IO) {
        connectionMutex.withLock {
            if (_connectionState.value == BluetoothConnectionState.CONNECTED) return@withLock

            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter?.cancelDiscovery()
            }

            closeSocket()

            _connectionState.value = BluetoothConnectionState.CONNECTING

            try {
                val hasConnectPermission = ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED

                val uuid = if (hasConnectPermission) {
                    device.uuids?.firstOrNull()?.uuid ?: SPP_UUID
                } else SPP_UUID

                socket = device.createRfcommSocketToServiceRecord(uuid)
                socket?.connect()

                val deviceName = if (hasConnectPermission) {
                    device.name ?: "Unknown Device"
                } else "Unknown Device"
                
                Log.d(TAG, "Connected to $deviceName")
                _connectionState.value = BluetoothConnectionState.CONNECTED

                listenForData()
            } catch (e: IOException) {
                Log.e(TAG, "Connection failed: ${e.message}")
                _connectionState.value = BluetoothConnectionState.ERROR
                closeSocket()
            }
        }
    }

    private suspend fun listenForData() = withContext(Dispatchers.IO) {
        val reader = socket?.inputStream?.bufferedReader() ?: return@withContext
        try {
            while (true) {
                val line = reader.readLine() ?: break
                if (line.isNotBlank()) {
                    _incomingData.emit(line)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Read error or disconnected: ${e.message}")
            if (_connectionState.value == BluetoothConnectionState.CONNECTED) {
                _connectionState.value = BluetoothConnectionState.ERROR
            }
        } finally {
            if (_connectionState.value == BluetoothConnectionState.ERROR) {
                closeSocket()
            } else {
                disconnect()
            }
        }
    }

    fun disconnect() {
        _connectionState.value = BluetoothConnectionState.DISCONNECTED
        closeSocket()
    }

    private fun closeSocket() {
        try {
            socket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket: ${e.message}")
        } finally {
            socket = null
        }
    }
}
