package com.andrea.rss.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface RssServiceWrapper {

    interface RssService {
        @GET(" ")
        suspend fun get(): String
    }

    private fun buildService(url: String): RssService {
        val retrofit = Retrofit.Builder()
            .baseUrl(url.formatBaseUrl())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        return retrofit.create(RssService::class.java)
    }

    private fun String.formatBaseUrl() = when (lastOrNull()) {
        null -> this
        '/' -> this
        else -> "$this/"
    }

    fun getNetworkService(url: String) = buildService(url)
}

class DefaultRssServiceWrapper : RssServiceWrapper