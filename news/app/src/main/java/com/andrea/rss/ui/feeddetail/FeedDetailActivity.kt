package com.andrea.rss.ui.feeddetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrea.rss.R

class FeedDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_detail)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                FeedDetailFragment.instance(intent.getIntExtra(FeedDetailFragment.FEED_ID, -1))
            ).commit()
    }
}