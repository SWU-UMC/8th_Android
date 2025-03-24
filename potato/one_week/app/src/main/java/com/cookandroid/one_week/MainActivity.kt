package com.cookandroid.one_week

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.cookandroid.one_week.databinding.ActivityMain2Binding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이미지 클릭 시 EmptyActivity로 이동
        binding.image1.setOnClickListener {
            startActivity(Intent(this, EmptyActivity::class.java))
        }
        binding.image2.setOnClickListener {
            startActivity(Intent(this, EmptyActivity::class.java))
        }
        binding.image3.setOnClickListener {
            startActivity(Intent(this, EmptyActivity::class.java))
        }
        binding.image4.setOnClickListener {
            startActivity(Intent(this, EmptyActivity::class.java))
        }
        binding.image5.setOnClickListener {
            startActivity(Intent(this, EmptyActivity::class.java))
        }
    }
}
