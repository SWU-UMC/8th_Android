package com.cookandroid.thread

import android.os.Bundle
import android.util.Log
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
import com.cookandroid.thread.ui.theme.ThreadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val a = A()
        val b = B()

        a.start()
        b.start()

    }

    class A : Thread() {  //쓰레드 실습!
        override fun run() {
            super.run()
            for(i in 1..1000){ //..이 <= <= 역할을 하다니... 지금껏.. 나는 왜 다 입력을..
                Log.d("test","first : $i")
            }

        }

    }

    class B : Thread() {  //쓰레드 실습!
        override fun run() {
            super.run()
            for(i in 1..1000){ //..이 <= <= 역할을 하다니... 지금껏.. 나는 왜 다 입력을..
                Log.d("test","second : $i")
            }

        }

    }
}

