package com.andrea.rss.repository

import com.andrea.rss.RSS_SAMPLE_DATA
import com.andrea.rss.database.DatabaseRssFeed
import com.andrea.rss.database.DatabaseRssItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class RssRepositoryTest {

    @Test
    fun `when an rss item is inserted, feeds are fetched`() = runBlocking {
        val repository = RssRepository.getInstance(
            FakeRssServiceWrapper(RSS_SAMPLE_DATA),
            FakeRssItemDao(),
            FakeRssFeedDao()
        )
        repository.itemsDao.insert(DatabaseRssItem(id = 3, name = "Rss sports", url = "www.example.com/link1", group = "Andrea News"))

        launch {
            repository.observeFeeds()
        }

        val expFeed1 = DatabaseRssFeed(
            guid = "1",
            title = "Headline 1",
            link = "https://www.example.com/fullstory/1",
            publicationDate = "Sun, 23 May 2021 14:34:05 GMT",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            rssItemId = 3)

        val expFeed2 = DatabaseRssFeed(
            guid = "2",
            title = "Headline 2",
            link = "https://www.example.com/fullstory/2",
            publicationDate = "Sun, 23 May 2021 14:15:37 GMT",
            description = "<img src=\"https://www.example.com/2.jpg\">Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            rssItemId = 3)

        val expFeed3 = DatabaseRssFeed(
            guid = "3",
            title = "Headline 3",
            link = "https://www.example.com/fullstory/3",
            publicationDate = "Sun, 23 May 2021 13:29:40 GMT",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            rssItemId = 3)

        val result = repository.feedsDao.getFeeds().first()
        assertTrue(result.containsAll(listOf(expFeed1, expFeed2, expFeed3)))
    }
}