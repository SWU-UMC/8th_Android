package com.example.myapplicationwe4324

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.study1.R 
import com.example.myapplicationwe4324.databinding.ActivityMainBinding // 자동 생성된 바인딩 클래스 import

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // 바인딩 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater) // 바인딩 객체 초기화
        setContentView(binding.root) // binding.root를 사용하여 레이아웃 설정

        binding.b1.setOnClickListener { // findViewById 대신 binding으로 뷰 접근
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        binding.b2.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        binding.b3.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        binding.b4.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        binding.b5.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets -> // findViewById 대신 binding으로 뷰 접근
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

