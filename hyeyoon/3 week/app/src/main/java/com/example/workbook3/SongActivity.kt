package com.example.workbook3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.workbook3.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {
    lateinit var binding: ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songDownIb.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java).apply {
                putExtra(MainActivity.STRING_INTENT_KEY, "IU 5th Album 'LILAC'")
            }
            setResult(Activity.RESULT_OK,intent)
            if(!isFinishing) finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songLikeIv.setOnClickListener {
            binding.songLikeIv.visibility = View.GONE
            binding.songLikeBackIv.visibility = View.VISIBLE
        }
        binding.songLikeBackIv.setOnClickListener {
            binding.songLikeBackIv.visibility = View.GONE
            binding.songLikeIv.visibility = View.VISIBLE
        }
        binding.songUnlikeIv.setOnClickListener {
            binding.songUnlikeIv.visibility = View.GONE
            binding.songUnlikeBackIv.visibility = View.VISIBLE
        }
        binding.songUnlikeBackIv.setOnClickListener {
            binding.songUnlikeBackIv.visibility = View.GONE
            binding.songUnlikeIv.visibility = View.VISIBLE
        }

        if(intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }
    }
    fun setPlayerStatus(isPlaying : Boolean) {
        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
        else {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
    }
}