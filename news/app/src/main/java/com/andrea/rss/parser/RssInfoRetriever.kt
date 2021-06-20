package com.andrea.rss.parser

import com.andrea.rss.network.DefaultRssServiceWrapper
import com.andrea.rss.network.RssServiceWrapper
import com.andrea.rss.util.schemeAndHost
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URI

internal suspend fun String.fetchRssItemInfo(service: RssServiceWrapper = DefaultRssServiceWrapper()): ParsedRssItem {
    val item = ParsedRssItem()

    try {
        val sh = URI(this).schemeAndHost()
        val head = Jsoup.parse(service.getNetworkService("${sh.first}://${sh.second}").get()).head()
        item.group = sh.second.replace(matchWWW, "")
        item.title = item.group // Use the group as the title. Will be replaced when fetching feeds for the first time
        item.iconUrl = getPreferredIconUrl(head)
        item.link = this
    } catch (e: Throwable) {
        throw RssFetchException("Error while fetching rss item info", e)
    }
    return item
}

private val matchWWW = "^(www\\d+.|www.)?".toRegex()

private fun getPreferredIconUrl(headEl: Element): String {
    var icons = headEl.getElementsByAttributeValue("rel", "shortcut icon")
    if (icons.isEmpty()) {
        icons = headEl.getElementsByAttributeValueContaining("rel", "icon")
    }
    return if (icons.isNotEmpty()) icons.first().attr("href") else ""
}