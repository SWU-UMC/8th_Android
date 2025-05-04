package com.example.workbook4

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.workbook4.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    companion object {const val STRING_INTENT_KEY = "my_string_key"}
    private var song:Song = Song()
    private var gson: Gson = Gson()

    private val songLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if (result.resultCode == RESULT_OK) {
            val returnString = result.data?.getStringExtra(STRING_INTENT_KEY)
            returnString?.let {
                Toast.makeText(this, "앨범 제목: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()

        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this,SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTie)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)
            songLauncher.launch(intent)
        }
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

    }

    private fun setMiniPlayer(song: Song){
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second * 100)/song.playTie
    }
    override fun onStart() {
        super.onStart() // 액티비티 전환될 때 onStart부터 시작
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // SharedPreference에 저장된 이름 가져옴
        val songJson = sharedPreferences.getString("songData", null) // 저장된 데이터 가져옴

        song = if(songJson == null) { // songJson이 null일 때 데이터를 직접 지정
            Song("라일락", "아이유(IU)", 0, 60, false, "lilac_iu")
        } else {
            gson.fromJson(songJson, Song::class.java) //songJson을 java 객체로 전환
        }

        setMiniPlayer(song) // miniplayer에 song 데이터 실제로 반영
    }
}