package com.andrea.rss.parser

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
    var imageUrls: List<String> = emptyList(),
    var publicationDate: String? = null,
    var description: String? = null,
) {
    fun isValid(): Boolean {
        /*
         * Sometimes, the description might not be present. In those cases, we assign
         * the value of the title to the description
         */
        description = description ?: title
        return guid != null && title != null && link != null
    }
}

data class ParseRss(val rssItem: ParsedRssItem?, val rssFeeds: List<ParsedRssFeed>)
