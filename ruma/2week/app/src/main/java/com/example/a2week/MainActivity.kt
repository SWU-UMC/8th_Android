package com.example.a2week

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.a2week.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)  // ViewBinding 초기화
        setContentView(binding.root)  // 뷰 설정

        // BottomNavigationView 설정
        val bottomNavigationView = binding.bottomNavigationView

        if(savedInstanceState==null){
            replaceFragment(HomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_music -> {
                    replaceFragment(MusicFragment())
                }

                R.id.nav_radio -> {
                    replaceFragment(RadioFragment())
                }
                R.id.nav_archive -> {
                    replaceFragment(ArchiveFragment())
                }
                R.id.nav_search -> {
                    replaceFragment(SearchFragment())
                }
            }
            true
        }
    }

    // Fragment 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment) // fragment_container로 교체
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
