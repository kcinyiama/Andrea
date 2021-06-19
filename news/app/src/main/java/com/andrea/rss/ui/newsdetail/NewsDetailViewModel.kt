package com.andrea.rss.ui.newsdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.repository.RssRepository

class NewsDetailViewModel internal constructor(private val rssRepository: RssRepository):ViewModel() {

      fun getFeedById(id: Int): LiveData<RssFeed?> =
        rssRepository.getFeed(id).asLiveData()

}