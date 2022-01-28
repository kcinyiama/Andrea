package com.andrea.rss.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrea.rss.database.getDatabase
import com.andrea.rss.network.DefaultRssServiceWrapper
import com.andrea.rss.repository.RssRepository
import retrofit2.HttpException

class FetchRssFeedWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "FetchRssFeedWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = RssRepository.getInstance(DefaultRssServiceWrapper(), database)

        return try {
            repository.observeFeeds()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}