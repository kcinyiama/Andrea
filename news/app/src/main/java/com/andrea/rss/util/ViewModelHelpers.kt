package com.andrea.rss.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrea.rss.database.getDatabase
import com.andrea.rss.network.DefaultRssServiceWrapper
import com.andrea.rss.repository.RssRepository
import com.andrea.rss.ui.main.MainViewModel

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val database = getDatabase(context.applicationContext)
            val repository = RssRepository.getInstance(DefaultRssServiceWrapper(), database.itemsDao, database.feedsDao)
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}