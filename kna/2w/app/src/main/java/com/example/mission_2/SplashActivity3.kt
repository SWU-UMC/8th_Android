package com.example.mission_2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mission_2.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash3)

        lifecycleScope.launch {
            delay(splashTimeOut)
            startActivity(Intent(this@SplashActivity3, SplashActivity::class.java))
            finish()
        }
    }
}