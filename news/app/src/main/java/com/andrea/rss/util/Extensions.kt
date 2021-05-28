package com.andrea.rss.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.toPrettyDate(): Date {
    val parser: DateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
    TODO()
}