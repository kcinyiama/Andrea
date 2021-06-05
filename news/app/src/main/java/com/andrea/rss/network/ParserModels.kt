package com.andrea.rss.network

data class ParsedRssItem(
    var title: String? = null,
    var link: String? = null,
    var group: String? = null,
    var iconUrl: String? = null
) {
    fun isValid() = title != null && link != null
}

data class ParsedRssFeed(
    var guid: String? = null,
    var title: String? = null,
    var link: String? = null,
    var publicationDate: String? = null,
    var description: String? = null,
) {
    fun isValid() = guid != null && title != null && link != null && description != null
}

data class ParseRss(val rssItem: ParsedRssItem?, val rssFeeds: List<ParsedRssFeed>)

enum class ItemTag {
    TITLE,
    LINK,
    GUID,
    DESCRIPTION,
    PUBLICATION_DATE,
    UNKNOWN;

    companion object {
        fun toEnum(str: String) = when (str) {
            "title" -> TITLE
            "link" -> LINK
            "guid" -> GUID
            "description" -> DESCRIPTION
            "pubDate" -> PUBLICATION_DATE
            else -> UNKNOWN
        }
    }
}

enum class ChannelTag {
    TITLE,
    LINK,
    UNKNOWN;

    companion object {
        fun toEnum(str: String) = when (str) {
            "title" -> TITLE
            "link" -> LINK
            else -> UNKNOWN
        }
    }
}

sealed class TagState
object ProcessingItem : TagState()
object ProcessingChannel : TagState()
object ProcessingUnknown : TagState()