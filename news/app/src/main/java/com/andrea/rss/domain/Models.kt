package com.andrea.rss.domain

import android.text.format.DateUtils
import com.andrea.rss.util.smartTruncate
import com.andrea.rss.util.toMillis

data class RssFeed(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val fullStoryLink: String,
    val publicationDate: String?,
    val description: String,
    val rssItemIconUrl: String?,
    val rssItemGroup: String,
    val rssItemTitle: String) {

    val shortTitle: String
        get() = title.smartTruncate(45)

    val prettyDate: CharSequence
        get() {
            return publicationDate?.toMillis()?.let {
                DateUtils.getRelativeTimeSpanString(it)
            } ?: "-"
        }
}