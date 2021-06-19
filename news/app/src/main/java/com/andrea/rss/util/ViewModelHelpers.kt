package com.andrea.rss.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrea.rss.database.getDatabase
import com.andrea.rss.network.DefaultRssServiceWrapper
import com.andrea.rss.repository.RssRepository
import com.andrea.rss.ui.main.MainViewModel
import com.andrea.rss.ui.newsdetail.NewsDetailViewModel

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val database = getDatabase(context.applicationContext)
        val repository = RssRepository.getInstance(DefaultRssServiceWrapper(), database.itemsDao, database.feedsDao)
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }else if(modelClass.isAssignableFrom(NewsDetailViewModel::class.java)){
            return NewsDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}