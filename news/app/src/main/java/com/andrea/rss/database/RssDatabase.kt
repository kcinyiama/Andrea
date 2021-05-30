package com.andrea.rss.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseRssItem::class, DatabaseRssFeed::class],
    version = 1,
    exportSchema = false
)
abstract class RssDatabase : RoomDatabase() {
    abstract val itemsDao: RssItemDao
    abstract val feedsDao: RssFeedDao
}

private lateinit var INSTANCE: RssDatabase

fun getDatabase(context: Context): RssDatabase {
    synchronized(RssDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    RssDatabase::class.java,
                    "rss_db"
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}