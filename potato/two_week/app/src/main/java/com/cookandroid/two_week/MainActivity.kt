package com.cookandroid.two_week

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_main)
        supportActionBar?.hide() // 액티비티 제목 숨기기

        // 예시로 Fragment1에서 Fragment2로 이동할 때 슬라이드 효과 추가
        /*val fragmentTransaction = supportFragmentManager.beginTransaction()

        // 슬라이드 애니메이션
        fragmentTransaction.setCustomAnimations(
            android.R.anim.slide_in_left,   // 화면에 등장할 때 애니메이션
            android.R.anim.slide_out_right  // 화면에서 사라질 때 애니메이션
        )*/



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 앱 실행 시 기본으로 표시할 Fragment 설정
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_shopping -> replaceFragment(ShoppingFragment())
                R.id.nav_like -> replaceFragment(LikeFragment())
                R.id.nav_list -> replaceFragment(ListFragment())
                R.id.nav_mypage -> replaceFragment(MypageFragment())
            }
            true
        }
    }

    // Fragment 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // 애니메이션 추가
            .replace(R.id.fragment_container, fragment) // 새로운 Fragment로 교체
            .commit() // 변경 사항 적용
        //페이드 아웃(fade out) 효과를 적용함.

    }
}
