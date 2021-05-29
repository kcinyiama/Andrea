package com.andrea.rss.database

import androidx.annotation.VisibleForTesting
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RssItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: DatabaseRssItem)

    @Query("SELECT * FROM rss_item")
    fun getItems(): Flow<List<DatabaseRssItem>>

    @Query("SELECT * FROM rss_item WHERE id = :id")
    fun getItem(id: Int): Flow<DatabaseRssItem>

    /*
     * This method requires Room to run two queries so Transaction
     * is added here to ensure the operation is performed atomically
     */
    @Transaction
    @Query("SELECT * FROM rss_item")
    fun getItemsWithFeeds(): Flow<List<DatabaseRssItemWithFeeds>>
}

@Dao
interface RssFeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg feeds: DatabaseRssFeed)

    @VisibleForTesting
    @Query("SELECT * FROM rss_feed")
    fun getFeeds(): Flow<List<DatabaseRssFeed>>

    @Update
    fun update(vararg feeds: DatabaseRssFeed)

    @Delete
    fun delete(vararg feeds: DatabaseRssFeed)

    @Transaction
    @Query("SELECT * FROM rss_feed WHERE id = :id")
    fun getFeedById(id: Int): Flow<DatabaseRssFeed>
}