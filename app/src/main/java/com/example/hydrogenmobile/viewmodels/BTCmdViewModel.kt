package com.example.hydrogenmobile.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.hydrogenmobile.models.BTModel

class BTCmdViewModel(private val context: BTModel) : ViewModel() {
    var showCmdPanel by mutableStateOf(false)
    var isReading = false
    var LpfStat = 0
    var SafStat = 0
    var MafStat = 0

    val SAFCmds = byteArrayOf(0x4D, 0x0A, 0x6A, 0xAA.toByte(), 0xCA.toByte(), 0xEA.toByte())
    val MAFCmds = byteArrayOf(0x2D, 0x2A, 0x4A, 0x8A.toByte())

    val SAFSamples = arrayOf(0, 2, 4, 8, 16, 32)
    val MAFSamples = arrayOf(0, 2, 4, 8)

    fun BTReadStart() {
        context.BTWriteCmd(0xCA.toByte(), 0x10)
        isReading = true
    }

    fun BTReadStop() {
        context.BTWriteCmd(0xCA.toByte(), 0x02)
        isReading = false
    }

    fun BTEnLowPassFilter() {
        context.BTWriteCmd(0xFA.toByte(), 0xFA.toByte())
        LpfStat = 1;
    }

    fun BTDisLowPassFilter() {
        context.BTWriteCmd(0xFD.toByte(), 0xFD.toByte())
        LpfStat = 0;
    }

    fun BTEn2SampleAverageFilter() {
        SafStat = 1
        context.BTWriteCmd(0xFA.toByte(), SAFCmds[SafStat])
    }

    fun BTSAFSampleChange() {
        SafStat = (SafStat % 5) + 1
        context.BTWriteCmd(0xFA.toByte(), SAFCmds[SafStat])
    }

    fun BTDisSampleAverageFilter() {
        SafStat = 0
        context.BTWriteCmd(0xFD.toByte(), SAFCmds[SafStat])
    }

    fun BTEn2MovingAverageFilter() {
        MafStat = 1
        context.BTWriteCmd(0xFA.toByte(), MAFCmds[MafStat])
    }

    fun BTMAFSampleChange() {
        MafStat = (MafStat % 3) + 1
        context.BTWriteCmd(0xFA.toByte(), MAFCmds[MafStat])
    }

    fun BTDisMovingAverageFilter() {
        MafStat = 0
        context.BTWriteCmd(0xFD.toByte(), MAFCmds[MafStat])
    }

    fun WriteExtraCmds(cmd: Byte) {
        MafStat = 0
        context.BTWriteCmd(0xCA.toByte(), cmd)
    }
}
