package com.andrea.rss.ui.main

import androidx.lifecycle.*
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.repository.RssRepository
import kotlinx.coroutines.launch

class MainViewModel internal constructor(private val rssRepository: RssRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            rssRepository.observeFeeds()
        }
    }

    val allFeeds: LiveData<List<RssFeed>> = rssRepository.allFeeds.asLiveData()

    private val _navigationToDetail = MutableLiveData<Int?>()

    val navigationToDetail: LiveData<Int?>
        get() = _navigationToDetail

    fun startNavigationToDetail(rssId: Int) {
        _navigationToDetail.also { it.value = rssId }
    }

    fun doneNavigating() = _navigationToDetail.also { it.value = null }
}