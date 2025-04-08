package com.example.a3week

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.a3week.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {
    lateinit var binding : ActivitySongBinding

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

        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }

        var isRepeat = false
        var isAllPlay = true

        binding.songRepeatIv.setOnClickListener {
            isRepeat = !isRepeat
            binding.songRepeatIv.setImageResource(
                if (isRepeat) R.drawable.nugu_btn_repeat_inactive
                else R.drawable.nugu_btn_repeat_inactive
            )
        }

        binding.songRandomIv.setOnClickListener {
            isAllPlay = !isAllPlay
            binding.songRandomIv.setImageResource(
                if (isAllPlay) R.drawable.nugu_btn_random_inactive
                else R.drawable.nugu_btn_random_inactive
            )
        }
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        } else {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
    }
}
