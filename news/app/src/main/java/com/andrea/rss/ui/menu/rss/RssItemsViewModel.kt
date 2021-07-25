package com.andrea.rss.ui.menu.rss

import androidx.lifecycle.*
import com.andrea.rss.domain.RssItem
import com.andrea.rss.repository.RssRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RssItemsViewModel internal constructor(private val rssRepository: RssRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                rssRepository.observeFeeds()
            }
        }
    }

    val allItems: LiveData<List<RssItem>> = rssRepository.allItems.asLiveData()

    private val _actionMode = MutableLiveData<Int?>()

    val actionMode: LiveData<Int?>
        get() = _actionMode

    fun startActionMode(rssId: Int): Boolean {
        _actionMode.value = rssId
        return true
    }

    fun endActionMode() {
        _actionMode.value = null
    }

    fun deleteItems(rssIds: List<Int>) {
        viewModelScope.launch {
            rssRepository.deleteItems(rssIds.toList())
        }
    }

    fun enableOrDisableItem(id: Int) {
        viewModelScope.launch {
            rssRepository.enableOrDisableItem(id)
        }
    }
}