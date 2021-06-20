package com.andrea.rss.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import java.net.URI
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}

fun String.toMillis(): Long? {
    val parser: DateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
    return runCatching {
        parser.parse(this)?.time
    }.getOrNull()
}

fun URI.schemeAndHost(): Pair<String, String> {
    val tempHost = host ?: path
    val slash = tempHost.indexOf('/')
    val cleanHost = if (slash != -1) tempHost.substring(0, slash) else tempHost
    return Pair(scheme ?: "https", cleanHost)
}

fun String.hostAndPath(): Pair<String, String> {
    val sh = URI(this).schemeAndHost()
    val query = replace("${sh.first}://${sh.second}/", "")
    return Pair("${sh.first}://${sh.second}/", query)
}