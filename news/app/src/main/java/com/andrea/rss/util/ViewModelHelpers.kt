package com.andrea.rss.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrea.rss.database.getDatabase
import com.andrea.rss.network.DefaultRssServiceWrapper
import com.andrea.rss.repository.RssRepository
import com.andrea.rss.ui.menu.rss.RssItemsViewModel
import com.andrea.rss.ui.main.MainViewModel
import com.andrea.rss.ui.feeddetail.FeedDetailViewModel
import com.andrea.rss.ui.menu.rss.NewRssItemViewModel

class AppViewModelFactory(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val database = getDatabase(context.applicationContext)
        val repository = RssRepository.getInstance(DefaultRssServiceWrapper(), database)

        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RssItemsViewModel::class.java) -> {
                RssItemsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NewRssItemViewModel::class.java) -> {
                NewRssItemViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FeedDetailViewModel::class.java) -> {
                FeedDetailViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}