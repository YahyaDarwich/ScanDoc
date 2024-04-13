package com.document.scanner.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class Utils {
    companion object {
        fun formatFileSize(fileSize: Long): String {
            if (fileSize <= 0) return "0 B"

            val units = arrayOf("B", "KB", "MB", "GB")
            val digitGroups = (log10(fileSize.toDouble()) / 3).toInt()

            val value = fileSize / 10.0.pow(digitGroups * 3)
            val unit = units[digitGroups]

            return String.format("%d %s", value.roundToInt(), unit)
        }


        @SuppressLint("SimpleDateFormat")
        fun longToDate(timestamp: Long): String {
            val formatter = SimpleDateFormat("dd MMM yyyy hh:mm aa")
            return formatter.format(Date(timestamp))
        }
    }
}