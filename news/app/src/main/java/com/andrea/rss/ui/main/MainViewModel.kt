package com.andrea.rss.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.repository.RssRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel internal constructor(private val rssRepository: RssRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                rssRepository.observeFeeds()
            }
        }
        // TODO Will be invoked when creating a new rss item from the UI
        insertRssItem()
    }

    val allFeeds: LiveData<List<RssFeed>> = rssRepository.allFeeds.asLiveData()

    private fun insertRssItem() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                /*
                 * Add an rss item to the db
                 *
                 * This will trigger the flow defined in rssRepository.observeFeeds() which
                 * will use the specified url to download and persist the rss feeds
                 *
                 * The allFeeds live data listen for changes in the db and widgets which observe it
                 * will be notified.
                 */
                // TODO Test rss links. To be deleted
                rssRepository.insertRssItem("https://www.thesundaily.my/rss/world")
                rssRepository.insertRssItem("https://www1.cbn.com/rss-cbn-news-health.xml")
                rssRepository.insertRssItem("https://thewest.com.au/travel/rss")
            }
        }
    }
}