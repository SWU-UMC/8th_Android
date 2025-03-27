package com.example.myapplicationwe4324

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.study1.R 
import com.example.myapplicationwe4324.databinding.ActivityTargetBinding // 자동 생성된 바인딩 클래스 import

class TargetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTargetBinding // 바인딩 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTargetBinding.inflate(layoutInflater) // 바인딩 객체 초기화
        setContentView(binding.root) // binding.root를 사용하여 레이아웃 설정
    }
}
