package com.andrea.rss.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FetchRssFeedWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testFetchRssFeedWorker() {

        val worker = TestListenableWorkerBuilder<FetchRssFeedWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}

















