package com.andrea.rss.repository

import com.andrea.rss.database.RssDatabase
import com.andrea.rss.database.toDomainModel
import com.andrea.rss.domain.RssFeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RssRepository(database: RssDatabase) {

    val allFeeds: Flow<List<RssFeed>> = database.itemsDao.getItemsWithFeeds().map { it.toDomainModel() }
}