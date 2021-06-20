package com.andrea.rss.parser

import com.andrea.rss.ICON_DATA_1
import com.andrea.rss.ICON_DATA_2
import com.andrea.rss.RSS_INCOMPLETE_SAMPLE_DATA
import com.andrea.rss.RSS_SAMPLE_DATA
import com.andrea.rss.repository.FakeRssServiceWrapper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RssParserTest {

    @Test
    fun `test correct size is parsed`() = assertEquals(3, RSS_SAMPLE_DATA.parseRss().rssFeeds.size)

    @Test
    fun `test data is parsed correctly`() {
        val parsedData = RSS_SAMPLE_DATA.parseRss()

        val item1 = ParsedRssItem(
            "World",
            "https://www.example.com/world"
        )

        val feed1 = ParsedRssFeed(
            "1",
            "Headline 1",
            "https://www.example.com/fullstory/1",
            listOf(),
            "Sun, 23 May 2021 14:34:05 GMT",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit")

        val feed2 = ParsedRssFeed(
            "2",
            "Headline 2",
            "https://www.example.com/fullstory/2",
            listOf("https://www.example.com/2.jpg"),
            "Sun, 23 May 2021 14:15:37 GMT",
            "<img src=\"https://www.example.com/2.jpg\">Lorem ipsum dolor sit amet, consectetur adipiscing elit")

        val feed3 = ParsedRssFeed(
            "3",
            "Headline 3",
            "https://www.example.com/fullstory/3",
            listOf("https://www.example.com/2.jpg", "https://www.example.com/3.jpg"),
            "Sun, 23 May 2021 13:29:40 GMT",
            "<img src=\"https://www.example.com/2.jpg\"><img src=\"https://www.example.com/3.jpg\">Lorem ipsum dolor sit amet, consectetur adipiscing elit")

        assertTrue(parsedData.rssItem == item1)
        assertTrue(parsedData.rssFeeds.containsAll(listOf(feed1, feed2, feed3)))
    }

    @Test
    fun `test incomplete rss feed is not added`() = assertEquals(1, RSS_INCOMPLETE_SAMPLE_DATA.parseRss().rssFeeds.size)

    @Test
    fun `test invalid data is not parsed`() = assertEquals(0, "Lorem".parseRss().rssFeeds.size)

    @Test
    fun `test rss item info is fetched correctly`() = runBlocking {
        val rssFeedUrl = "http://www.example.com/rss"
        val info = rssFeedUrl.fetchRssItemInfo(FakeRssServiceWrapper(ICON_DATA_1))
        assertEquals("example.com", info.group)
        assertEquals("www.example.com/2.png", info.iconUrl)
    }

    @Test
    fun `test alternative icon is fetched if preferred is not available`() = runBlocking {
        val rssFeedUrl = "http://www.rss.example1.com/rss"
        val info = rssFeedUrl.fetchRssItemInfo(FakeRssServiceWrapper(ICON_DATA_2))
        assertEquals("rss.example1.com", info.group)
        assertEquals("www.example.com/alternative1.png", info.iconUrl)
    }

    @Test
    fun `test attempt to parse invalid urls correctly`() = runBlocking {
        val service = FakeRssServiceWrapper("")

        var rssFeedUrl = "example1.com/rss"
        assertEquals("example1.com", rssFeedUrl.fetchRssItemInfo(service).group)

        rssFeedUrl = "www.example1.com/rss"
        assertEquals("example1.com", rssFeedUrl.fetchRssItemInfo(service).group)

        rssFeedUrl = "www1.example1.com/rss"
        assertEquals("example1.com", rssFeedUrl.fetchRssItemInfo(service).group)

        rssFeedUrl = "http://example1.com/rss"
        assertEquals("example1.com", rssFeedUrl.fetchRssItemInfo(service).group)

        rssFeedUrl = "http://www1.example1.com/rss"
        assertEquals("example1.com", rssFeedUrl.fetchRssItemInfo(service).group)
    }
}