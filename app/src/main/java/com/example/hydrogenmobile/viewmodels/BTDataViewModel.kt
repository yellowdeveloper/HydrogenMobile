    package com.example.hydrogenmobile.viewmodels

    import android.util.Log
    import androidx.compose.runtime.mutableStateListOf
    import androidx.compose.runtime.toMutableStateList
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.hydrogenmobile.models.BTModel
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch
    import com.example.hydrogenmobile.models.BTDataParser
    import com.example.hydrogenmobile.models.BTDataParser.PROTOCOL_ERROR
    import com.example.hydrogenmobile.models.DataStats
    import com.example.hydrogenmobile.models.FilterData
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.SharingStarted
    import kotlinx.coroutines.flow.flow
    import kotlinx.coroutines.flow.stateIn
    import java.util.LinkedList
    import kotlin.math.sqrt


    class BTDataViewModel(private val context: BTModel) : ViewModel() {

        // Calculate Stats of Filter Values
        // windowSize = Recent Buffer Size for Stat Calculation (ex. Recent 100 data for stat calculation)
        // ...Buffer = Buffer to Store Recent Data for Stat Calculation
        private val _windowSize = MutableStateFlow(100)
        val windowSize: StateFlow<Int> = _windowSize.asStateFlow()

        val rawBuffer = List(_windowSize.value) { 0 }.toMutableStateList()
        val lpfBuffer = List(_windowSize.value) { 0 }.toMutableStateList()
        val safBuffer = List(_windowSize.value) { 0 }.toMutableStateList()
        val mafBuffer = List(_windowSize.value) { 0 }.toMutableStateList()

        // Collect Original Data from BTModel
        // originalData = receive array (Updates every BT receive)
        // finalData = processed data for UI update
        private var originalData = ByteArray(0)

        private val _finalData = MutableStateFlow<FilterData?>(null)
        val finalData: StateFlow<FilterData?> = _finalData.asStateFlow()

        val XTickUpdater = flow {
            while (true) {
                emit(System.currentTimeMillis())
                delay(1000)
            }
        }

        val xTickTime = XTickUpdater.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            System.currentTimeMillis()
        )

        init {
            viewModelScope.launch {
                context.dataReceived.collect { data: ByteArray ->

                    originalData += data

                    while(true)
                    {
                        val parsedData = BTDataParser.parse(originalData)

                        if (parsedData[0] !=  PROTOCOL_ERROR && parsedData[1] !=  PROTOCOL_ERROR)
                        {
                            originalData = originalData.sliceArray(parsedData[4] until originalData.size)
                            Log.d("BTDataViewModel", "dataParsed: ${parsedData.contentToString()}")

                            updateBuffer(parsedData)

                            _finalData.value = setUiUpdateData(parsedData)
                            // Log.d("BTDataViewModel", "outputData1: ${_finalData.value?.raw?.current}")

                            continue
                        }
                        else if (parsedData[0] == PROTOCOL_ERROR && parsedData[1] != PROTOCOL_ERROR)
                        {
                            originalData = originalData.sliceArray(parsedData[1] until originalData.size)
                            Log.e("BTDataViewModel", "Invalid Length: Clear ReceivedBuffer")

                            continue
                        }
                        else
                        {
                            break
                        }
                    }
                }
            }
        }

        fun setUiUpdateData(parsedData: IntArray): FilterData {
            val result = FilterData(
                raw = statCalculation(rawBuffer, parsedData[0]),
                saf = statCalculation(safBuffer, parsedData[1]),
                lpf = statCalculation(lpfBuffer, parsedData[2]),
                maf = statCalculation(mafBuffer, parsedData[3])
            )

            return result
        }

        private fun updateBuffer(parsedData: IntArray) {
            addToBuffer(rawBuffer, parsedData[0])
            addToBuffer(safBuffer, parsedData[1])
            addToBuffer(lpfBuffer, parsedData[2])
            addToBuffer(mafBuffer, parsedData[3])
        }

        private fun addToBuffer(list: MutableList<Int>, value: Int) {
            list.add(value)
            while (list.size > _windowSize.value) {
                list.removeAt(0)
            }
        }

        fun statCalculation(buffer: MutableList<Int>, newVal: Int): DataStats {
            val cur = newVal
            var sum: Long = 0
            var maxVal = Int.MIN_VALUE
            var minVal = Int.MAX_VALUE

            for (i in buffer) {
                sum += i
                maxVal = maxOf(maxVal, i)
                minVal = minOf(minVal, i)
            }

            val avg = sum / buffer.size
            val ptp = maxVal - minVal

            var variance = 0.0

            for (i in buffer) {
                variance += (i - avg).toDouble() * (i - avg)
            }
            variance /= buffer.size

            val std = sqrt(variance)

            val result = DataStats(
                current = cur,
                average = avg.toInt(),
                max = maxVal,
                min = minVal,
                peakToPeak = ptp,
                stDev = std.toInt()
            )

            return result
        }


        fun YTickUpdater() {

        }
    }