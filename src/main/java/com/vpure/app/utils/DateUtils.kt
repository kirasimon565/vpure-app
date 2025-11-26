package com.vpure.app.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    fun formatTimestamp(date: Date): String {
        return timeFormat.format(date)
    }

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }
}
