package com.andrea.rss.repository

import android.util.Log
import com.andrea.rss.database.*
import com.andrea.rss.network.RssServiceWrapper
import com.andrea.rss.parser.*
import com.andrea.rss.util.hostAndPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class RssRepository private constructor(
    private val network: RssServiceWrapper,
    val itemsDao: RssItemDao,
    val feedsDao: RssFeedDao
) {

    companion object {
        // We want a singleton instance of the repository
        @Volatile
        private var instance: RssRepository? = null

        fun getInstance(network: RssServiceWrapper, itemsDao: RssItemDao, feedsDao: RssFeedDao) =
            instance ?: synchronized(this) {
                instance ?: RssRepository(network, itemsDao, feedsDao).also { instance = it }
            }
    }

    val allFeeds = itemsDao.getItemsWithFeeds().map { it.toDomainModel() }

    val allItems = itemsDao.getItems().map { it.toDomainModel() }

    fun getFeed(id: Int) = itemsDao.getFeedWithItemById(id).map { it.toDomainModel() }

    suspend fun observeFeeds() {
        // TODO News will be fetched once a day unless it's forced fetch (Subject to change)
        itemsDao.geItemsByFetchStatus()
            .distinctUntilChanged()
            .collect {
                coroutineScope {
                    it.forEach { item ->
                        withContext(Dispatchers.IO) {
                            val rssInfo = network.getNetworkService(item.url).get().parseRss()
                            saveRssInfo(dbItem = item, parsedInfo = rssInfo)
                        }
                    }
                }
            }
    }

    suspend fun validateRssUrl(url: String, block: (bool: Boolean) -> Unit) {
        coroutineScope {
            withContext(Dispatchers.IO) {
                try {
                    val rssItem = url.fetchRssItemInfo(network)
                    val hp = url.hostAndPath()
                    val rssInfo = network.getNetworkService(hp.first).get(hp.second).parseRss()

                    when (rssInfo.rssFeeds.isNotEmpty()) {
                        true -> {
                            block(true)
                            saveRssInfo(parsedItem = rssItem, parsedInfo = rssInfo)
                        }
                        else -> block(false)
                    }
                    return@withContext
                } catch (e: Exception) {
                    Log.e("Repo", e.message!!)
                }
                block(false)
            }
        }
    }

    suspend fun enableOrDisableItem(id: Int) {
        coroutineScope {
            withContext(Dispatchers.IO) {
                itemsDao.getItemById(id).take(1).collect { item ->
                    item?.let {
                        it.enabled = if (it.enabled == 0) 1 else 0
                        itemsDao.update(it)
                    }
                }
            }
        }
    }

    suspend fun deleteItems(rssIds: List<Int>) {
        coroutineScope {
            withContext(Dispatchers.IO) {
                itemsDao.delete(rssIds)
            }
        }
    }

    private suspend fun saveRssInfo(
        parsedItem: ParsedRssItem? = null,
        dbItem: DatabaseRssItem? = null,
        parsedInfo: ParseRss
    ) {
        var databaseItem = dbItem ?: parsedItem!!.toDatabaseModel()
        parsedInfo.rssItem?.let {
            databaseItem.name = it.title ?: databaseItem.name
        }
        databaseItem.fetched = 1
        databaseItem = itemsDao.insertAndGet(databaseItem)
        feedsDao.insert(*parsedInfo.rssFeeds.toDatabaseModel(databaseItem).toTypedArray())
    }
}
