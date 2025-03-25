package com.example.mission_2

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainer)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        bottomNavigationView.selectedItemId = R.id.menu_home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                    true
                }
                R.id.menu_neighbor -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, NeighborFragment())
                        .commit()
                    true
                }
                R.id.menu_map -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, MapFragment())
                        .commit()
                    true
                }
                R.id.menu_message -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, MessageFragment())
                        .commit()
                    true
                }
                R.id.menu_my -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, MyFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

    }
}