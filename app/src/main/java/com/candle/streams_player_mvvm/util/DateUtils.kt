package com.candle.streams_player_mvvm.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
     fun getDateTime(s: String): String? {
         val sdf = SimpleDateFormat("MM/dd/yyyy")
         try {
            val netDate = Date(s as Long * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
             val netDate = Date()
             return sdf.format(netDate)
        }
    }
}