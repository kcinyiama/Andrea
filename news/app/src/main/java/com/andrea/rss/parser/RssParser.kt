package com.andrea.rss.parser

import org.jsoup.Jsoup
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.ByteArrayInputStream
import java.util.*
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

private fun createRssHandler(xmlString: String) =
    RssFeedHandler().apply {
        runCatching {
            val parser = createSAXParserFactory().newSAXParser()
            parser.parse(InputSource(ByteArrayInputStream(xmlString.toByteArray())), this)
        }
    }

private class RssFeedHandler : DefaultHandler() {
    companion object {
        const val CHANNEL = "channel"
        const val ITEM = "item"
    }

    private lateinit var currentTag: String
    private var feedTags = ArrayDeque<Node>()

    private lateinit var tempItem: ParsedRssItem
    private lateinit var tempFeed: ParsedRssFeed

    private val feeds = mutableListOf<ParsedRssFeed>()

    override fun characters(ch: CharArray, start: Int, length: Int) {
        when (feedTags.peek()?.parent?.name) {
            CHANNEL -> {
                buildChars(ch, start, length) {
                    processChannelData(it, ChannelTag.toEnum(currentTag))
                }
            }
            ITEM -> {
                buildChars(ch, start, length) {
                    processItemData(it, ItemTag.toEnum(currentTag))
                }
            }
        }
    }

    private inline fun buildChars(ch: CharArray, start: Int, length: Int, block: (str: String) -> Unit) {
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
            ItemTag.AUTHOR -> author = author.getVal(str)
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
        feedTags.apply {
            if (size == 0) push(Node(qName, null)) else push(Node(qName, peek()))
        }

        when (qName) {
            CHANNEL -> tempItem = ParsedRssItem()
            ITEM -> tempFeed = ParsedRssFeed()
        }
        currentTag = qName
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        feedTags.pop()

        when (qName) {
            CHANNEL -> {
                if (!tempItem.isValid()) {
                    throw RssParserException("Parsed rss item is not valid: $tempItem")
                }
            }
            ITEM -> {
                if (tempFeed.isValid()) {
                    extractImages()
                    feeds += tempFeed
                }
            }
        }
    }

    private fun extractImages() {
        val images = mutableListOf<String>()
        val elements = Jsoup.parse(tempFeed.description).getElementsByTag("img")
        for (tag in elements) {
            tag.attr("src").takeIf { it.isNotEmpty() }?.let { images.add(it) }
        }
        tempFeed.imageUrls = images
    }

    fun getRss(): ParseRss {
        val item: ParsedRssItem? = if (this::tempItem.isInitialized) tempItem else null
        return ParseRss(item, feeds)
    }
}

internal fun String.parseRss(): ParseRss = when (isNullOrEmpty()) {
    false -> createRssHandler(this).getRss()
    else -> throw RssParserException("String to parse cannot be null or empty")
}