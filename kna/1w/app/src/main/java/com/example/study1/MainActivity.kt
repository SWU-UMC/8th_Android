package com.example.myapplicationwe4324

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.study1.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        //intent를 활용하여 앱 내에서 다른 화면(액티비티)으로 이동하거나 데이터를 전달
        val imageView = findViewById<ImageView>(R.id.b1)
        imageView.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        val imageView2 = findViewById<ImageView>(R.id.b2)
        imageView2.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        val imageView3 = findViewById<ImageView>(R.id.b3)
        imageView3.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        val imageView4 = findViewById<ImageView>(R.id.b4)
        imageView4.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
        val imageView5 = findViewById<ImageView>(R.id.b5)
        imageView5.setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

class TargetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)
    }
}