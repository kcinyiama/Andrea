package com.andrea.rss.repository

import android.util.Log
import com.andrea.rss.database.*
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.network.RssServiceWrapper
import com.andrea.rss.network.parseRssFeeds
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
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
        itemsDao.getItemsWithFeeds().map { it.toDomainModel() }

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

    private suspend fun fetchAndInsertFeeds(item: DatabaseRssItem) {
        Log.e("Repo", "Fetching for " + item.id)
        val feeds = network.getNetworkService(item.url).getFeeds().parseRssFeeds()
        if (feeds.isNotEmpty()) {
            Log.e("Repo", "Inserting for " + item.id)
            feedsDao.insert(*feeds.toDatabaseModel(item).toTypedArray())
        }
        item.fetched = 1
        itemsDao.update(item)
    }
}