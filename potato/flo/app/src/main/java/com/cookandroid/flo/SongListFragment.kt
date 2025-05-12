package com.cookandroid.flo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.flo.databinding.FragmentSongBinding
import com.google.gson.Gson
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//수록곡 실행 코드
class SongListFragment : Fragment() {

    private lateinit var binding: FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        // 모든 재생 버튼 → music_lilac 재생
        val dummySong = SaveSong(
            title = "라일락",
            singer = "아이유 (IU)",
            coverImg = R.drawable.img_album_exp2,
            isChecked = false,
            isLike = false,
            id = 1,
            music = "music_lilac",
            playtime = 60,
            isPlaying = true,
            second = 0
        )

        val playAction = View.OnClickListener {
            val spf = requireActivity().getSharedPreferences("song", AppCompatActivity.MODE_PRIVATE)
            val editor = spf.edit()
            editor.putString("songData", Gson().toJson(dummySong))
            editor.putInt("songId", dummySong.id)
            editor.apply()

            (activity as? MainActivity)?.setMiniPlayer(dummySong)
        }
        binding.songPlay01Iv.setOnClickListener(playAction)
        binding.songPlay02Iv.setOnClickListener(playAction)
        binding.songPlay03Iv.setOnClickListener(playAction)
        binding.songPlay04Iv.setOnClickListener(playAction)

        return binding.root
    }
}