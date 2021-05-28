package com.andrea.rss.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel() {
    val liveData: LiveData<SplashScreenState>
        get() = mutableLiveData
    private val mutableLiveData = MutableLiveData<SplashScreenState>()

    init {
        viewModelScope.launch {
            delay(3000)
            mutableLiveData.postValue(SplashScreenState.OpenMainActivity())
        }
    }
}

sealed class SplashScreenState {
    class OpenMainActivity : SplashScreenState()
}
