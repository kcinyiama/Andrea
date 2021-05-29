package com.andrea.rss.network

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.ByteArrayInputStream
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory

data class ParsedRssFeed(
    val guid: String,
    val title: String,
    val link: String,
    val publicationDate: String,
    val description: String
)

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

private fun createRssFeedHandler(xmlString: String) = RssFeedHandler().apply {
    runCatching {
        val parser = createSAXParserFactory().newSAXParser()
        parser.parse(InputSource(ByteArrayInputStream(xmlString.toByteArray())), this)
    }
}

private class RssFeedHandler : DefaultHandler() {
    companion object {
        const val ITEM = "item"
        const val TITLE = "title"
        const val GUID = "guid"
        const val LINK = "link"
        const val PUB_DATE = "pubDate"
        const val DESCRIPTION = "description"
    }

    private val feeds = mutableListOf<ParsedRssFeed>()

    private var processingItemTag = false
    private var currentTag: String? = null
    private val charBuilder = StringBuilder()

    private val rssItems = arrayOfNulls<String>(5)

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (processingItemTag) {
            val value = charBuilder.append(ch, start, length).toString().trim()
            if (value.isNotEmpty()) {
                when (currentTag) {
                    GUID -> rssItems.setValue(0, value)
                    TITLE -> rssItems.setValue(1, value)
                    LINK -> rssItems.setValue(2, value)
                    PUB_DATE -> rssItems.setValue(3, value)
                    DESCRIPTION -> rssItems.setValue(4, value)
                }
            }
        }
        charBuilder.clear()
    }

    private fun Array<String?>.setValue(index: Int, value: String) {
        if (this[index] == null) {
            this[index] = value
        } else {
            this[index] += value
        }
    }

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes
    ) {
        if (ITEM == qName) {
            processingItemTag = true
        }
        currentTag = qName
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        super.endElement(uri, localName, qName)
        if (ITEM == qName) {
            processingItemTag = false
            addItem()
        }
    }

    private fun addItem() {
        if (isRssValid()) {
            feeds += ParsedRssFeed(
                guid = rssItems[0]!!,
                title = rssItems[1]!!,
                link = rssItems[2]!!,
                publicationDate = rssItems[3]!!,
                description = rssItems[4]!!)
        }
        rssItems.fill(null)
    }

    private fun isRssValid() = rssItems.count { it == null } == 0

    fun getFeeds(): List<ParsedRssFeed> = feeds
}

internal fun String.parseRssFeeds(): List<ParsedRssFeed> = when (isNullOrEmpty()) {
    false -> createRssFeedHandler(this).getFeeds()
    else -> listOf()
}