package com.example.mission_2

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.mission_2.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater) // 뷰 바인딩 초기화
        setContentView(binding.root) // 뷰 바인딩의 root를 ContentView로 설정

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        val fragmentContainer: FrameLayout = binding.fragmentContainer

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        bottomNavigationView.selectedItemId = R.id.menu_home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    replaceFragment(HomeFragment(), R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.menu_neighbor -> {
                    replaceFragment(NeighborFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.menu_map -> {
                    replaceFragment(MapFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.menu_message -> {
                    replaceFragment(MessageFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.menu_my -> {
                    replaceFragment(MyFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, enterAnim: Int, exitAnim: Int): Boolean {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}