package com.andrea.rss.domain

import android.text.format.DateUtils
import androidx.core.text.HtmlCompat
import com.andrea.rss.util.toMillis

data class RssFeed(
    val id: Int,
    val title: String,
    val imageUrls: List<String>,
    val fullStoryLink: String,
    val publicationDate: String?,
    val description: String,
    val rssItem: RssItem
) {

    val headline: String
        get() {
            val shortDesc = description.removeImgTag().take(150)
            // Using toString because we don't want any styles to be applied here
            return HtmlCompat.fromHtml(shortDesc, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        }

    val prettyDate: CharSequence
        get() {
            return publicationDate?.toMillis()?.let {
                DateUtils.getRelativeTimeSpanString(it)
            } ?: "-"
        }
}

data class RssItem(val id: Int, var name: String, val group: String, val enabled: Boolean, val iconUrl: String?)

val regex = "<img.+?>".toRegex()
fun String.removeImgTag() = replace(regex, "")