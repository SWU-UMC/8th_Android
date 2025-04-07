package com.example.workbook3

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
            finish()
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