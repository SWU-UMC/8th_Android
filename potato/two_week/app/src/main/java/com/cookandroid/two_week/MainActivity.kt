package com.cookandroid.two_week

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cookandroid.two_week.databinding.AcitivityMainBinding

class MainActivity : AppCompatActivity() {

    // 뷰 바인딩 객체 선언
    private lateinit var binding: AcitivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 객체 초기화
        binding = AcitivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide() // 액티비티 제목 숨기기

        // 앱 실행 시 기본으로 표시할 Fragment 설정
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // BottomNavigationView 클릭 리스너 설정
        binding.bottomNavigation.setOnItemSelectedListener { item ->
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
    }
}