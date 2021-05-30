package com.andrea.rss.database

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RssDatabaseTest {

    private lateinit var itemsDao: RssItemDao
    private lateinit var feedsDao: RssFeedDao
    private lateinit var db: RssDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, RssDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        itemsDao = db.itemsDao
        feedsDao = db.feedsDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetRssItems() = runBlocking {
        val item1 = DatabaseRssItem(name = "Rss sports")
        val item2 = DatabaseRssItem(name = "Rss fashion")
        val item3 = DatabaseRssItem(name = "Rss technology")

        itemsDao.insert(item1, item2, item3)
        val items = itemsDao.getItems().first()
        assertTrue(items.size == 3)

        val exp1 = DatabaseRssItem(1, "Rss sports")
        val exp2 = DatabaseRssItem(2, "Rss fashion")
        val exp3 = DatabaseRssItem(3, "Rss technology")

        assertTrue(items.containsAll(listOf(exp1, exp2, exp3)))
    }

    @Test
    fun testInsertAndGetRssFeeds() = runBlocking {
        // Save the rss items
        val item1 = DatabaseRssItem(name = "Rss sports")
        val item2 = DatabaseRssItem(name = "Rss fashion")
        itemsDao.insert(item1, item2)

        val feed1 = DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 1", rssItemId = 1)
        val feed2 = DatabaseRssFeed(guid = "guid2", title = "Rss sport feed 2", rssItemId = 1)
        val feed3 = DatabaseRssFeed(guid = "guid3", title = "Rss fashion feed 1", rssItemId = 2)
        val feed4 = DatabaseRssFeed(guid = "guid4", title = "Rss fashion feed 2", rssItemId = 2)
        feedsDao.insert(feed1, feed2, feed3, feed4)

        val feeds = feedsDao.getFeeds().first()
        assertTrue(feeds.size == 4)

        val exp1 = DatabaseRssFeed(id = 1, guid = "guid1", title = "Rss sport feed 1", rssItemId = 1)
        val exp2 = DatabaseRssFeed(id = 2, guid = "guid2", title = "Rss sport feed 2", rssItemId = 1)
        val exp3 = DatabaseRssFeed(id = 3, guid = "guid3", title = "Rss fashion feed 1", rssItemId = 2)
        val exp4 = DatabaseRssFeed(id = 4, guid = "guid4", title = "Rss fashion feed 2", rssItemId = 2)
        assertTrue(feeds.containsAll(listOf(exp1, exp2, exp3, exp4)))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun testInsertRssFeedWithoutItemFails() {
        feedsDao.insert(DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 1", rssItemId = 1))
    }

    @Test
    fun testDuplicateGuidIsReplaced() = runBlocking {
        itemsDao.insert(DatabaseRssItem(name = "Rss sports"))
        feedsDao.insert(DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 1", rssItemId = 1))
        feedsDao.insert(DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 2", rssItemId = 1))

        val items = feedsDao.getFeeds().first()
        val exp1 = DatabaseRssFeed(id = 2, guid = "guid1", title = "Rss sport feed 2", rssItemId = 1)
        assertTrue(items.size == 1)
        assertEquals(exp1, items.first())
    }

    @Test
    fun testInsertAndGetRssFeed() = runBlocking {
        itemsDao.insert(DatabaseRssItem(name = "Rss sports"))
        feedsDao.insert(DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 1", rssItemId = 1))

        val savedFeed = feedsDao.getFeedById(1).first()
        assertNotNull(savedFeed)

        val expFeed1 = DatabaseRssFeed(id = 1, guid = "guid1", title = "Rss sport feed 1", rssItemId = 1)
        assertEquals(expFeed1, savedFeed)
    }

    @Test
    fun testInsertAndUpdateRssFeed() = runBlocking {
        itemsDao.insert(DatabaseRssItem(name = "Rss sports"))
        feedsDao.insert(DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 1", rssItemId = 1))

        var savedFeed = feedsDao.getFeedById(1).first()
        savedFeed.guid = "guid2"
        savedFeed.title = "Rss sport feed issue 2"
        feedsDao.update(savedFeed)

        savedFeed = feedsDao.getFeedById(1).first()
        val expFeed1 = DatabaseRssFeed(id = 1, guid = "guid2", title = "Rss sport feed issue 2", rssItemId = 1)
        assertEquals(expFeed1, savedFeed)
    }

    @Test
    fun testInsertAndDeleteRssFeed() = runBlocking {
        itemsDao.insert(DatabaseRssItem(name = "Rss sports"))
        feedsDao.insert(DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 1", rssItemId = 1))

        var savedFeed: DatabaseRssFeed? = feedsDao.getFeedById(1).first()
        feedsDao.delete(savedFeed!!)

        savedFeed = feedsDao.getFeedById(1).firstOrNull()
        assertNull(savedFeed)
    }

    @Test
    fun testInsertAndGetRssItemsWithFeeds() = runBlocking {
        // Save the rss items
        val item1 = DatabaseRssItem(name = "Rss sports")
        val item2 = DatabaseRssItem(name = "Rss fashion")
        itemsDao.insert(item1, item2)

        val feed1 = DatabaseRssFeed(guid = "guid1", title = "Rss sport feed 1", rssItemId = 1)
        val feed2 = DatabaseRssFeed(guid = "guid2", title = "Rss sport feed 2", rssItemId = 1)
        val feed3 = DatabaseRssFeed(guid = "guid3", title = "Rss fashion feed 1", rssItemId = 2)
        val feed4 = DatabaseRssFeed(guid = "guid4", title = "Rss fashion feed 2", rssItemId = 2)
        feedsDao.insert(feed1, feed2, feed3, feed4)

        val itemsWithFeeds = itemsDao.getItemsWithFeeds().first()
        assertTrue(itemsWithFeeds.size == 2)

        val expItem1 = DatabaseRssItem(1, "Rss sports")
        val expItem2 = DatabaseRssItem(2, "Rss fashion")

        val expFeed1 = DatabaseRssFeed(id = 1, guid = "guid1", title = "Rss sport feed 1", rssItemId = 1)
        val expFeed2 = DatabaseRssFeed(id = 2, guid = "guid2", title = "Rss sport feed 2", rssItemId = 1)
        val expFeed3 = DatabaseRssFeed(id = 3, guid = "guid3", title = "Rss fashion feed 1", rssItemId = 2)
        val expFeed4 = DatabaseRssFeed(id = 4, guid = "guid4", title = "Rss fashion feed 2", rssItemId = 2)

        itemsWithFeeds.forEach {
            when (it.item.id) {
                1 -> {
                    assertEquals(expItem1, it.item)
                    assertTrue(it.feeds.containsAll(listOf(expFeed1, expFeed2)))
                }
                2 -> {
                    assertEquals(expItem2, it.item)
                    assertTrue(it.feeds.containsAll(listOf(expFeed3, expFeed4)))
                }
                else -> fail("No rss items with feeds was returned")
            }
        }
    }
}