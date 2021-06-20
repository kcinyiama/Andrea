package com.andrea.rss.ui.menu.rss

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.rss.repository.RssRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewRssItemViewModel internal constructor(private val rssRepository: RssRepository) : ViewModel() {
    
    private val _spinner = MutableLiveData<Boolean?>()
    
    val spinner: LiveData<Boolean?>
        get() = _spinner
    
    private val _urlValidator = MutableLiveData<Boolean?>()

    val urlValidator: LiveData<Boolean?>
        get() = _urlValidator
    
    fun saveRssUrl(url: String) {
        _spinner.value = true

        viewModelScope.launch {
            rssRepository.validateRssUrl(url) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        _urlValidator.value = if (it) true else null
                    }
                }
            }
            _spinner.value = null
        }
    }
}