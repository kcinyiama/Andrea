package com.andrea.rss.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.andrea.rss.ui.main.MainActivity
import com.andrea.rss.R


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashScreenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        viewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
        viewModel.liveData.observe(this, Observer {
            when (it) {
                is SplashScreenState.OpenMainActivity -> {
                    launchMainAcivity()
                }
            }
        })
    }

    private fun launchMainAcivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}