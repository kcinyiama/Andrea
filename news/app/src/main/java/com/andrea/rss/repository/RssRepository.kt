package com.andrea.rss.repository

import android.util.Log
import com.andrea.rss.database.*
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.network.RssServiceWrapper
import com.andrea.rss.network.fetchRssItemInfo
import com.andrea.rss.network.parseRss
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    val allFeeds: Flow<List<RssFeed>> =
        itemsDao.getItemsWithFeeds().map {
            it.toDomainModel()
        }


    suspend fun observeFeeds() {
        // TODO News will be fetched once a day unless it's forced fetch (Subject to change)
        itemsDao.geItemsByFetchStatus()
            //.distinctUntilChanged()
            .collect {
                coroutineScope {
                    it.forEach { item ->
                        launch {
                            fetchAndInsertFeeds(item)
                        }
                    }
                }
            }
    }

    suspend fun insertRssItem(rssUrl: String) {
        val rssItem = rssUrl.fetchRssItemInfo(network)
        Log.e("Repo", "Fetched rss info $rssItem")
        itemsDao.insert(rssItem.toDatabaseModel())
    }

    private suspend fun fetchAndInsertFeeds(item: DatabaseRssItem) {
        Log.e("Repo", "Fetching for " + item.id)
        val rssInfo = network.getNetworkService(item.url).get().parseRss(true)
        if (rssInfo.rssFeeds.isNotEmpty()) {
            Log.e("Repo", "Inserting for " + item.id)
            feedsDao.insert(*rssInfo.rssFeeds.toDatabaseModel(item).toTypedArray())
        }
        item.fetched = 1
        itemsDao.update(item)
    }
}
