package com.example.hydrogenmobile.models

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
class BTModel(private val context: Context) {
    init {
        Log.d("BTMODEL", "created ${hashCode()}")
    }
    // Cast SystemService to BluetoothManager type and get adapter
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter
    private val scanner = adapter?.bluetoothLeScanner

    // variable to store connection state
    private var bluetoothGatt: BluetoothGatt? = null
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // variables for bluetooth communication
    private val serviceUUID = UUID.fromString("7f2fa2dc-2871-4e9e-9328-00372d10e04f")
    private val integerUUID = UUID.fromString("a594f566-4481-4297-b817-9b58025116ef")
    private val _dataReceived = MutableSharedFlow<ByteArray>(
        replay = 1,
        extraBufferCapacity = 64
    )
    val dataReceived: SharedFlow<ByteArray> = _dataReceived.asSharedFlow()


    // inherit BluetoothGattCallback to override callback functions
    private val gattCallback = object : BluetoothGattCallback() {

        // Callback: When connection state changes
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _isConnected.value = true
                Log.d("BTConnect", "Bluetooth Connected")

                val mtuRequest = gatt.requestMtu(247)

                if (!mtuRequest) {
                    Log.e("BTModel", "MTU request failed to start, proceeding to discover services")
                    gatt.discoverServices()
                }

                // gatt.discoverServices()
            }
            else {
                _isConnected.value = false
                closeGatt()
            }
        }

        // Callback: When MTU changes
        @SuppressLint("MissingPermission")
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BTModel", "MTU successfully changed to: $mtu")
            }
            else {
                Log.e("BTModel", "MTU Change failed, status: $status")
            }

            Log.d("BTModel", "Starting Service Discovery...")
            gatt.discoverServices()
        }

        // Callback: When service discovery is finished
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            // Enable notification when service is successfully discovered
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt?.getService(serviceUUID)
                val characteristic = service?.getCharacteristic(integerUUID)

                if (characteristic != null) {
                    gatt.setCharacteristicNotification(characteristic, true)

                    val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))

                    descriptor?.let {
                        @Suppress("DEPRECATION")
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        @Suppress("DEPRECATION")
                        gatt.writeDescriptor(it)
                    }
                }
                else {
                    Log.e("BTModel", "Target Characteristic not found error")
                }
            }
            else {
                Log.w("BTModel", "onServicesDiscovered received: $status")
            }
        }

        // Callback: When characteristic value changes(data received)
        @SuppressLint("MissingPermission")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic) {
            @Suppress("DEPRECATION")
            val dataBytes = characteristic.value

            Log.d("BTModel", "dataReceived: ${dataBytes.joinToString(" ") { "%02X".format(it) }}")
            // Log.d("BTModel", "dataReceived: $dataBytes")

            _dataReceived.tryEmit(dataBytes)
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        closeGatt()
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        _isConnected.value = false
    }

    @SuppressLint("MissingPermission")
    fun ScanDevices(): Flow<ScanState> = callbackFlow {
        trySend(ScanState.loading)

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.let { trySend(ScanState.success(it)) }
            }
            override fun onScanFailed(errorCode: Int) {
                trySend(ScanState.failed(Exception("Scan failed with error code: $errorCode")))
                close()
            }
        }

        if (adapter.isEnabled) {
            scanner?.startScan(callback)
        }
        else {
            trySend(ScanState.failed(BluetoothDisabledException()))
            close()
            // need to add new function :: show enable bluetooth dialog
        }

        awaitClose {
            if (adapter.isEnabled) {
                scanner?.stopScan(callback)
            }
        }
    }

    // cmd type :: 0xCA (control), 0xFA (filter)
    fun BTWriteCmd(CmdType: Byte ,CmdVal: Byte) {
        val gatt = bluetoothGatt ?: return

        val service = gatt?.getService(serviceUUID)
        val characteristic = service?.getCharacteristic(integerUUID)
        Log.d("btn", "BTWriteCmd")
        characteristic?.let {
            Log.d("btn", "characteristic?.let")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                gatt.writeCharacteristic(
                    it,
                    byteArrayOf( 0x09, 0x0D, 0x09, 0x0D ,CmdType, CmdVal, 0x27, 0x22, 0x27, 0x22),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                // 구버전 방식
                it.value = byteArrayOf( 0x09, 0x0D, 0x09, 0x0D ,CmdType, CmdVal, 0x27, 0x22, 0x27, 0x22)
                it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                gatt.writeCharacteristic(it)
            }
        }
    }
}