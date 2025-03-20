package com.example.week_1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 감정 이미지뷰 가져오기
        val smileImage = findViewById<ImageView>(R.id.smileImage)
        val excitedImage = findViewById<ImageView>(R.id.excitedImage)
        val sosoImage = findViewById<ImageView>(R.id.sosoImage)
        val sadImage = findViewById<ImageView>(R.id.sadImage)
        val angryImage = findViewById<ImageView>(R.id.angryImage)


        // 클릭 이벤트 추가
        smileImage.setOnClickListener {
            val intent = Intent(this, NextActivity::class.java)
            startActivity(intent)
        }
    }
}