package com.andrea.rss.network

import com.andrea.rss.RSS_INCOMPLETE_SAMPLE_DATA
import com.andrea.rss.RSS_SAMPLE_DATA
import org.junit.Test
import org.junit.Assert.*

class RssParserTest {

    @Test
    fun testCorrectSizeIsParsed() = assertEquals(3, RSS_SAMPLE_DATA.parseRssFeeds().size)

    @Test
    fun `test data is parsed correctly`() {
        val parsedData = RSS_SAMPLE_DATA.parseRssFeeds()

        val feed1 = ParsedRssFeed(
            "1",
            "Headline 1",
            "https://www.example.com/fullstory/1",
            "Sun, 23 May 2021 14:34:05 GMT",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit")

        val feed2 = ParsedRssFeed(
            "2",
            "Headline 2",
            "https://www.example.com/fullstory/2",
            "Sun, 23 May 2021 14:15:37 GMT",
            "<img src=\"https://www.example.com/2.jpg\">Lorem ipsum dolor sit amet, consectetur adipiscing elit")

        val feed3 = ParsedRssFeed(
            "3",
            "Headline 3",
            "https://www.example.com/fullstory/3",
            "Sun, 23 May 2021 13:29:40 GMT",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit")

        assertTrue(parsedData.containsAll(listOf(feed1, feed2, feed3)))
    }

    @Test
    fun `test incomplete data is not added`() = assertEquals(1, RSS_INCOMPLETE_SAMPLE_DATA.parseRssFeeds().size)

    @Test
    fun `test invalid data is not parsed`() = assertEquals(0, "Lorem".parseRssFeeds().size)
}