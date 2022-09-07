package com.candle.streams_player_mvvm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.candle.streams_player_mvvm.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        waitForWhile()
    }

    private fun waitForWhile() {
        Handler(Looper.getMainLooper()).postDelayed({
            callNextActivity()
        }, 3000)

    }

    private fun callNextActivity() {
        val intent = Intent(this,MainActivity :: class.java)
        startActivity(intent)
        finish()
    }
}