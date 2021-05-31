package com.andrea.rss.database

import androidx.annotation.VisibleForTesting
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RssItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg item: DatabaseRssItem)

    @Update
    suspend fun update(vararg feeds: DatabaseRssItem)

    @Query("SELECT * FROM rss_item")
    fun getItems(): Flow<List<DatabaseRssItem>>

    @Query("SELECT * FROM rss_item WHERE fetched = 0")
    fun geItemsByFetchStatus(): Flow<List<DatabaseRssItem>>

    @Query("SELECT * FROM rss_item WHERE id = :id")
    fun getItemById(id: Int): Flow<DatabaseRssItem?>

    @Query("SELECT * FROM rss_item WHERE url = :url")
    fun getItemByUrl(url: String): DatabaseRssItem?

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
    suspend fun insert(vararg feeds: DatabaseRssFeed)

    @VisibleForTesting
    @Query("SELECT * FROM rss_feed")
    fun getFeeds(): Flow<List<DatabaseRssFeed>>

    @Update
    suspend fun update(vararg feeds: DatabaseRssFeed)

    @Delete
    suspend fun delete(vararg feeds: DatabaseRssFeed)

    @Transaction
    @Query("SELECT * FROM rss_feed WHERE id = :id")
    fun getFeedById(id: Int): Flow<DatabaseRssFeed?>
}