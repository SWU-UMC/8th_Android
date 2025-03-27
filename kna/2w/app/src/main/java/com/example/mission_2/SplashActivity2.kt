package com.example.mission_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.mission_2.databinding.ActivitySplashBinding

class SplashActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash2)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SplashActivity3::class.java))
            finish()
        }, splashTimeOut)
    }
}