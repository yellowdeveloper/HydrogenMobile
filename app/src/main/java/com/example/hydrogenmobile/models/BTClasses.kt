package com.example.hydrogenmobile.models

import android.bluetooth.le.ScanResult
import java.util.LinkedList

sealed class ScanState {
    object loading : ScanState()
    data class success(val result: ScanResult): ScanState()
    data class failed(val exception: Exception): ScanState()
}

class BluetoothDisabledException : Exception("Bluetooth is disabled")

sealed class BtUiEvent {
    object RequestEnableBluetooth : BtUiEvent()
}

data class DataStats (
    val current: Int = 0,
    val average: Int = 0,
    val max: Int = 0,
    val min: Int = 0,
    val peakToPeak: Int = 0,
    val stDev: Int = 0
)

data class FilterData (
    val raw: DataStats = DataStats(),
    val saf: DataStats = DataStats(),
    val lpf: DataStats = DataStats(),
    val maf: DataStats = DataStats(),
)