package com.andrea.rss.ui.feeddetail

import android.content.Context
import android.content.res.Resources
import android.text.Html
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.lifecycle.*
import com.andrea.rss.R
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.repository.RssRepository
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedDetailViewModel internal constructor(private val rssRepository: RssRepository) :
    ViewModel() {

    companion object {
        private const val WIDTH_MARGIN = 60
        private const val HEIGHT_DIVISOR = 3
    }

    private val _feed = MutableLiveData<RssFeed>()

    val feed: LiveData<RssFeed>
        get() = _feed

    fun getFeedById(id: Int): LiveData<RssFeed?> = rssRepository.getFeed(id).asLiveData()

    fun fetchMedia(context: Context, feed: RssFeed) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val imageGetter = Html.ImageGetter {
                    val drawable = runCatching {
                        Glide.with(context).asDrawable().load(it).submit().get()
                    }.getOrDefault(AppCompatResources.getDrawable(context, R.drawable.ic_empty))

                    when (drawable) {
                        null -> null
                        else -> {
                            drawable.setBounds(
                                0,
                                0,
                                Resources.getSystem().displayMetrics.widthPixels - WIDTH_MARGIN,
                                Resources.getSystem().displayMetrics.heightPixels / HEIGHT_DIVISOR
                            )
                            drawable
                        }
                    }
                }

                feed.description = HtmlCompat.fromHtml(
                    feed.description.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY,
                    imageGetter,
                    null
                )
                withContext(Dispatchers.Main) {
                    _feed.value = feed
                }
            }
        }
    }
}