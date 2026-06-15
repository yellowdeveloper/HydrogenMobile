package com.example.hydrogenmobile

import android.app.Application
import com.example.hydrogenmobile.models.BTModel

class BTInstance : Application() {
    val btModel by lazy {
        BTModel(this)
    }
}