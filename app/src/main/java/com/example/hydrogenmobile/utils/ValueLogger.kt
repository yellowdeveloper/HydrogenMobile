package com.example.hydrogenmobile.utils

import android.R.attr.mimeType
import android.content.ContentValues
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.content.Context
import androidx.compose.material3.contentColorFor
import kotlin.apply
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

var isLogging by mutableStateOf(false)
private val logBuilder = java.lang.StringBuilder()

fun LoggingOn() {
    isLogging = true
    logBuilder.append("date/time\traw\tSAF\tLPF\tMAF\n")
}

fun saveLog(context: Context, dataToWrite:String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyMMdd_HHmmss")
    val timeStamp = LocalDateTime.now().format(formatter)
    val fileName = "Log$timeStamp.tsv"

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "text/tab-separated-values")
        put(MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOCUMENTS + "/HydroLogs")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Files.getContentUri("external"),
        values
    ) ?: return false

    return try {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(dataToWrite.toByteArray())
        }
        true
    }catch (e:Exception) {
        false
    }
}

fun AddLogTxt(data: String) {
    logBuilder.append(data)
}

fun LoggingOff(context: Context) {
    logBuilder.append(
            "\nAVG\t=AVERAGE(B2:INDEX(B:B, ROW()-2))\t=AVERAGE(C2:INDEX(C:C, ROW()-2))\t=AVERAGE(D2:INDEX(D:D, ROW()-2))\t=AVERAGE(E2:INDEX(E:E, ROW()-2))" +
            "\nMIN\t=MIN(B2:INDEX(B:B, ROW()-3))\t=MIN(C2:INDEX(C:C, ROW()-3))\t=MIN(D2:INDEX(D:D, ROW()-3))\t=MIN(E2:INDEX(E:E, ROW()-3))" +
            "\nMAX\t=MAX(B2:INDEX(B:B, ROW()-4))\t=MAX(C2:INDEX(C:C, ROW()-4))\t=MAX(D2:INDEX(D:D, ROW()-4))\t=MAX(E2:INDEX(E:E, ROW()-4))" +
            "\nSTDEV\t=STDEV(B2:INDEX(B:B, ROW()-5))\t=STDEV(C2:INDEX(C:C, ROW()-5))\t=STDEV(D2:INDEX(D:D, ROW()-5))\t=STDEV(E2:INDEX(E:E, ROW()-5))" +
            "\nMIN-MAX DIFF\t=INDEX(B:B, ROW()-2)-INDEX(B:B, ROW()-3)\t=INDEX(C:C, ROW()-2)-INDEX(C:C, ROW()-3)\t=INDEX(D:D, ROW()-2)-INDEX(D:D, ROW()-3)\t=INDEX(E:E, ROW()-2)-INDEX(E:E, ROW()-3)"
    )

    val finalData = logBuilder.toString()
    saveLog(context, finalData)

    logBuilder.clear()
    isLogging = false
}