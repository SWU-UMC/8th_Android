package com.cookandroid.one_week

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cookandroid.one_week.ui.theme.One_weekTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main_2)

        // 이미지 클릭 시 EmptyActivity로 이동
        findViewById<ImageView>(R.id.image1).setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.image2).setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.image3).setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.image4).setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.image5).setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
        }

    }
}

