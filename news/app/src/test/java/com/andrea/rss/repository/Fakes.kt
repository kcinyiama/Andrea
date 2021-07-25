package com.andrea.rss.repository

import com.andrea.rss.database.*
import com.andrea.rss.network.RssServiceWrapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

class FakeRssServiceWrapper(val result: String) : RssServiceWrapper {

    override fun getNetworkService(url: String): RssServiceWrapper.RssService {
        return object : RssServiceWrapper.RssService {
            override suspend fun get(path: String) = result
        }
    }
}

class FakeRssItemDao : RssItemDao {
    private val rssItemChannel = Channel<List<DatabaseRssItem>>(capacity = Channel.BUFFERED)

    override suspend fun insert(vararg item: DatabaseRssItem) {
        rssItemChannel.send(item.toList())
    }

    override suspend fun update(vararg feeds: DatabaseRssItem) {
        val items = rssItemChannel.receive()
        rssItemChannel.send(feeds.toList().intersect(items).toList())
    }

    override fun getItems(): Flow<List<DatabaseRssItem>> = flow {
        // Nothing to emit
    }

    override fun geItemsByFetchStatus(): Flow<List<DatabaseRssItem>> = flow {
        val items = rssItemChannel.receive()
        rssItemChannel.send(items)
        val result = items.filter { it.fetched == 0 }
        emit(result)
    }

    override fun getItemById(id: Int): Flow<DatabaseRssItem?> {
        TODO("Not yet implemented")
    }

    override fun getLastInsertedItem(): DatabaseRssItem = runBlocking {
        val items = rssItemChannel.receive()
        rssItemChannel.send(items)
        return@runBlocking items.last()
    }

    override fun getItemByUrl(url: String): DatabaseRssItem? {
        TODO("Not yet implemented")
    }

    override fun getItemsWithFeeds(): Flow<List<DatabaseRssItemWithFeeds>> = flow {
        // Nothing to emit
    }

    override fun getFeedWithItemById(feedId: Int): Flow<DatabaseFeedWithItem> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(ids: List<Int>) {
        TODO("Not yet implemented")
    }
}

class FakeRssFeedDao : RssFeedDao {
    private val rssFeedChannel = Channel<List<DatabaseRssFeed>>(capacity = Channel.BUFFERED)

    override suspend fun insert(vararg feeds: DatabaseRssFeed) {
        rssFeedChannel.send(feeds.toList())
    }

    override fun getFeeds(): Flow<List<DatabaseRssFeed>> = flow {
        emit(rssFeedChannel.receive())
    }

    override suspend fun update(vararg feeds: DatabaseRssFeed) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(vararg feeds: DatabaseRssFeed) {
        TODO("Not yet implemented")
    }

    override fun getFeedById(id: Int): Flow<DatabaseRssFeed?> {
        TODO("Not yet implemented")
    }

}