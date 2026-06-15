package com.example.hydrogenmobile.models

import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.xor

object BTDataParser {

    private val Header = byteArrayOf(0x09,0x0D,0x09,0x0D)
    private val Footer = byteArrayOf(0x27,0x22,0x27,0x22)

    private const val NO_DATA = 8388610
    public const val PROTOCOL_ERROR = -8388610

    fun parse(data: ByteArray): IntArray {
        val indexArray = protocolCheck(data)

        if (indexArray[0] == PROTOCOL_ERROR && indexArray[1] != PROTOCOL_ERROR)
        {
            Log.e("BTDataParser", "Data Crashed CRC or Length invalid... Clear Buffer")
            return indexArray
        }

        if (indexArray[0] == PROTOCOL_ERROR) // && indexArray[1] == PROTOCOL_ERROR)
        {
            Log.w("BTDataParser", "No Header found... receive more data")
            return indexArray
        }

        if (indexArray[1] == PROTOCOL_ERROR) // && indexArray[0] != PROTOCOL_ERROR)
        {
            Log.w("BTDataParser", "No Footer found... receive more data try count: ")
            return indexArray
        }

        return filterCheck(data, indexArray)
    }

    fun protocolCheck (data: ByteArray): IntArray {

        val result: IntArray = IntArray(2)

        val headerIndex = data.indexOfArray(0, Header)
        result[0] = headerIndex

        if ( headerIndex != -1)
        {
            val footerIndex = data.indexOfArray(headerIndex + 4, Footer)
            val dataLength = data[headerIndex + 4]
            result[1] = footerIndex

            if (footerIndex != -1)
            {
                val dataSize = footerIndex - headerIndex - 4
                if (dataSize - 2 == dataLength.toInt())
                {
                    val crcIndex = footerIndex - 1
                    if (calCRC(data[crcIndex], data.sliceArray(headerIndex + 5 until crcIndex)) == false)
                    {
                        result[0] = PROTOCOL_ERROR
                        result[1] = footerIndex
                    }
                    return result
                    // return data.sliceArray(headerIndex + 4 until footerIndex)
                }
                else
                {
                    result[0] = PROTOCOL_ERROR
                    result[1] = footerIndex
                    return result
                }
            }
            else
            {
                result[1] = PROTOCOL_ERROR
                return result
            }
        }
        else
        {
            result[0] = PROTOCOL_ERROR
            result[1] = PROTOCOL_ERROR
            return result
        }
    }

    fun filterCheck(data: ByteArray, indexArray: IntArray): IntArray
    {
        // pass length byte follows after header
        val tmpArray = data.sliceArray(indexArray[0] + 5 until indexArray[1] - 1)
        val buffer = ByteBuffer.wrap(tmpArray).order(ByteOrder.LITTLE_ENDIAN)
        val numOfElements: Int = (tmpArray.size / 5) + 1

        val resultArray = IntArray(5) {NO_DATA}

        val rawBytes = ByteArray(3)
        buffer.get(rawBytes)

        val raw = getDigitalCount(rawBytes)

        resultArray[0] = raw

        while (buffer.remaining() >= 5) {
            val numOfFilter = buffer.get().toInt()
            val value = -buffer.int

            resultArray[numOfFilter + 1] = value
        }

        resultArray[4] = indexArray[1]

        return resultArray
    }

    fun ByteArray.indexOfArray(start: Int, array: ByteArray): Int {
        if (array.isEmpty() || this.size < array.size) return -1

        for (i in start..this.size - array.size) {
            var exist = true
            for (j in array.indices) // indices = 0..array.size - 1
            {
                if (this[i+j] != array[j]) {
                    exist = false
                    break
                }
            }
            if (exist)return i
        }
        return -1
    }

    fun getDigitalCount(data: ByteArray): Int
    {
        var dc: Int
        dc = (((data[0].toInt() and 0xFF) shl 16)
                or ((data[1].toInt() and 0xFF) shl 8)
                or (data[2].toInt() and 0xFF))
        dc = dc shl 8
        dc = dc shr 8
        return -dc
    }

    fun calCRC(crcVal: Byte, data: ByteArray): Boolean
    {
        var crc = 0
        val poly = 0x07

        for (b in data) {
            crc = crc xor (b.toInt() and 0xFF)

            repeat(8) {
                crc =
                    if ((crc and 0x80) != 0)
                        ((crc shl 1) xor poly) and 0xFF
                    else
                        (crc shl 1) and 0xFF
            }
        }

        if (crc.toByte() == crcVal) return true
        return false
    }

//    fun convertByteArray(data: ByteArray): Int
//    {
//        val res = ByteBuffer.wrap(data)
//            .order(ByteOrder.LITTLE_ENDIAN)
//            .int
//        return -res
//    }
}