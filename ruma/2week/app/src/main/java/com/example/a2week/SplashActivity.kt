package com.example.a2week

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 3초 뒤에 MainActivity로 넘어가기
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // 스플래시 화면을 종료시켜 MainActivity로 전환
        }, 3000)  // 3000ms = 3초
    }
}
