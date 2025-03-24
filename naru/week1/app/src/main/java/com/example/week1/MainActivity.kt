package com.example.week1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.week1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 뷰 바인딩을 위한 변수 선언
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩을 사용하여 xml 레이아웃과 연결
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // findViewById(R.id.main) -> binding.main 변경
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 각 이미지뷰 클릭 시 해당 문자열을 넘기도록 설정
        setClickListener(binding.ivMainHappy, R.string.tv_main_happy)
        setClickListener(binding.ivMainExciting, R.string.tv_main_exciting)
        setClickListener(binding.ivMainNormal, R.string.tv_main_normal)
        setClickListener(binding.ivMainAnxiety, R.string.tv_main_anxiety)
        setClickListener(binding.ivMainUpset, R.string.tv_main_upset)
    }

    /*
    클릭 리스너 설정하는 함수
    특정 이미지뷰를 클릭했을 때 문자열까지 넘겨주는 함수
    @param ivMood 클릭할 이미지뷰
    @param tvMood 클릭 시 전달할 문자열 리소스 아이디
    */
    private fun setClickListener(ivMood: android.view.View, tvMood: Int) {
        ivMood.setOnClickListener {
            val mood = getString(tvMood)  // strings.xml에 저장된 감정 문자열 가져오기
            moveNextActivity(mood)  // 다음 액티비티로 데이터 전달
        }
    }

    /*
    액티비티 전환 함수
    @param mood 사용자가 선택한 감정 텍스트
    */
    private fun moveNextActivity(mood: String) {
        val intent = Intent(this, NextActivity::class.java)
        intent.putExtra("mood", mood)  // Intent에 "mood"라는 키로 감정 문자열 추가
        startActivity(intent)  // NextActivity 실행
    }
}