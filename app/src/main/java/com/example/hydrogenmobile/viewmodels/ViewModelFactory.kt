package com.example.hydrogenmobile.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hydrogenmobile.models.BTModel

class ViewModelFactory (private val btModel: BTModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BTScanViewModel::class.java)) {
            return BTScanViewModel(btModel) as T
        }
        if (modelClass.isAssignableFrom(BTDataViewModel::class.java)) {
            return BTDataViewModel(btModel) as T
        }
        if (modelClass.isAssignableFrom(BTCmdViewModel::class.java)) {
            return BTCmdViewModel(btModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}