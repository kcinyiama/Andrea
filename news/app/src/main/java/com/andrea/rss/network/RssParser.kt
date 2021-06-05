package com.andrea.rss.network

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.ByteArrayInputStream
import java.net.URI
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory

private fun createSAXParserFactory() = SAXParserFactory.newInstance().apply {
    runCatching {
        // Some protection against XEE attacks
        setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        setFeature("http://xml.org/sax/features/external-general-entities", false)
        setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }
}

private fun createRssHandler(xmlString: String, parseRssItem: Boolean) =
    RssFeedHandler(parseRssItem).apply {
        runCatching {
            val parser = createSAXParserFactory().newSAXParser()
            parser.parse(InputSource(ByteArrayInputStream(xmlString.toByteArray())), this)
        }
    }

private class RssFeedHandler(private val parseRssItem: Boolean) : DefaultHandler() {
    companion object {
        const val CHANNEL = "channel"
        const val ITEM = "item"
    }

    private var processingTag: TagState = ProcessingUnknown
    private lateinit var currentTag: String

    private lateinit var tempItem: ParsedRssItem
    private lateinit var tempFeed: ParsedRssFeed

    private val feeds = mutableListOf<ParsedRssFeed>()

    override fun characters(ch: CharArray, start: Int, length: Int) {
        when (processingTag) {
            ProcessingChannel -> {
                buildChars(ch, start, length) {
                    processChannelData(it, ChannelTag.toEnum(currentTag))
                }
            }
            ProcessingItem -> {
                buildChars(ch, start, length) {
                    processItemData(it, ItemTag.toEnum(currentTag))
                }
            }
            ProcessingUnknown -> { /* Nothing to process */
            }
        }
    }

    private fun buildChars(ch: CharArray, start: Int, length: Int, block: (str: String) -> Unit) {
        val value = StringBuilder().append(ch, start, length).toString().trim()
        if (value.isNotEmpty()) {
            block(value)
        }
    }

    private fun processChannelData(str: String, tag: ChannelTag) = with(tempItem) {
        when (tag) {
            ChannelTag.TITLE -> title = title.getVal(str)
            ChannelTag.LINK -> link = link.getVal(str)
            else -> { /* Do nothing */
            }
        }
    }

    private fun processItemData(str: String, tag: ItemTag) = with(tempFeed) {
        when (tag) {
            ItemTag.TITLE -> title = title.getVal(str)
            ItemTag.LINK -> link = link.getVal(str)
            ItemTag.GUID -> guid = guid.getVal(str)
            ItemTag.DESCRIPTION -> description = description.getVal(str)
            ItemTag.PUBLICATION_DATE -> publicationDate = publicationDate.getVal(str)
            else -> { /* Do nothing */ }
        }
    }

    private fun String?.getVal(str: String) = if (isNullOrEmpty()) str else this + str

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes
    ) {
        processingTag = when (qName) {
            CHANNEL -> {
                when (parseRssItem) {
                    true -> {
                        tempItem = ParsedRssItem()
                        ProcessingChannel
                    }
                    false -> ProcessingUnknown
                }
            }
            ITEM -> {
                tempFeed = ParsedRssFeed()
                ProcessingItem
            }
            else -> {
                processingTag
            }
        }
        currentTag = qName
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        when (qName) {
            CHANNEL -> {
                if (parseRssItem) {
                    if (!tempItem.isValid()) {
                        throw RssParserException("Parsed rss item is not valid: $tempItem")
                    }
                }
            }
            ITEM -> {
                if (tempFeed.isValid()) {
                    feeds += tempFeed
                }
            }
        }
    }

    fun getRss(): ParseRss {
        val item: ParsedRssItem? = if (this::tempItem.isInitialized) tempItem else null
        return ParseRss(item, feeds)
    }
}

internal fun String.parseRss(parseRssItem: Boolean = false): ParseRss = when (isNullOrEmpty()) {
    false -> createRssHandler(this, parseRssItem).getRss()
    else -> throw RssParserException("String to parse cannot be null or empty")
}

internal suspend fun String.fetchRssItemInfo(service: RssServiceWrapper = DefaultRssServiceWrapper()): ParsedRssItem {
    val item = ParsedRssItem()
    runCatching {
        val uri = URI(this)
        val host = cleanHost(uri)
        val scheme = uri.scheme ?: "http"

        val head = Jsoup.parse(service.getNetworkService("$scheme://$host").get()).head()
        item.group = host.replace(matchWWW, "")
        item.title = item.group // Use the group as the title. Will be replaced when fetching feeds for the first time
        item.iconUrl = getPreferredIconUrl(head)
        item.link = this
    }.onFailure {
        Log.d("RssParser", "Error while fetching rss item info: ${it.message}")
    }
    return item
}

private fun cleanHost(uri: URI): String {
    val host = uri.host ?: uri.path
    val slash = host.indexOf('/')
    return if (slash != -1) host.substring(0, slash) else host
}

private val matchWWW = "^(www\\d+.|www.)?".toRegex()

private fun getPreferredIconUrl(headEl: Element): String {
    var icons = headEl.getElementsByAttributeValue("rel", "shortcut icon")
    if (icons.isEmpty()) {
        icons = headEl.getElementsByAttributeValueContaining("rel", "icon")
    }
    return if (icons.isNotEmpty()) icons.first().attr("href") else ""
}