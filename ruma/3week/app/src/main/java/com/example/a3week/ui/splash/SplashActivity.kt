package com.example.a3week.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.a3week.ui.main.MainActivity
import com.example.a3week.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) { // override 키워드 및 Bundle? 파라미터 추가
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater) // binding 초기화
        setContentView(binding.root)

        val handler= Handler(Looper.getMainLooper())
        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        },1000)
    }
}