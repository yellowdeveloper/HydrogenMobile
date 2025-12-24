package com.example.hydrogenmobile.viewmodels

import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydrogenmobile.models.BTModel
import com.example.hydrogenmobile.models.BluetoothDisabledException
import com.example.hydrogenmobile.models.BtUiEvent
import com.example.hydrogenmobile.models.ScanState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BTScanViewModel(private val context: BTModel) : ViewModel() {

    private val _scanned_devices = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanned_devices: StateFlow<List<ScanResult>> = _scanned_devices.asStateFlow()

    private val _eventFlow = MutableSharedFlow<BtUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val isConnected: StateFlow<Boolean> = context.isConnected

    private var scan_job: Job? = null

    fun connectDevice(result: ScanResult) {
        viewModelScope.launch {
            ScanStop()
            context.connectToDevice(result.device)
        }
    }

    fun disconnectDevice() {
        context.disconnect()
    }

    fun ScanStart() {
        if (scan_job?.isActive == true) return

        scan_job = viewModelScope.launch {
            context.ScanDevices()
                .catch{e -> Log.e("ScanVM", "Error: ${e.message}")}
                .collect {
                    state ->
                    when(state) {
                        is ScanState.loading -> {
                            Log.d("ScanVM", "Scanning started...")
                        }

                        is ScanState.success -> {
                            val newResult = state.result

                            val currentList = _scanned_devices.value
                            val isDuplicate = currentList.any { it.device.address == newResult.device.address }

                            if (!isDuplicate && newResult.device.name != null) {
                                _scanned_devices.value = currentList + newResult
                            }
                        }

                        is ScanState.failed -> {
                            if (state.exception is BluetoothDisabledException) {
                                _eventFlow.emit(BtUiEvent.RequestEnableBluetooth)
                            } else {
                                Log.e("ScanVM", "Scan Failed: ${state.exception.message}")
                            }
                        }
                    }
                }
        }
    }

    fun ScanStop() {
        scan_job?.cancel()
        scan_job = null
    }
}