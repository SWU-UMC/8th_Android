package com.example.mission_2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mission_2.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash2)

        lifecycleScope.launch {
            delay(splashTimeOut)
            startActivity(Intent(this@SplashActivity2, SplashActivity3::class.java))
            finish()
        }
    }
}