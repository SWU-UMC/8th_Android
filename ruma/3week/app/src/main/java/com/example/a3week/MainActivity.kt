package com.example.a3week

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.a3week.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var song: Song = Song()
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_FLO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val message = data.getStringExtra("message")
                    Log.d("message", message!!)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.mainPlayerCl.setOnClickListener {
            val editor=getSharedPreferences("song",MODE_PRIVATE).edit()
            editor.putInt("songId",song.id)
            editor.apply()

            val intent=Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
        inputDummySongs()
        initBottomNavigation()


    }

    private fun initBottomNavigation() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commit() // commitAllowingStateLoss() -> commit() 으로 변경

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commit() // commitAllowingStateLoss() -> commit() 으로 변경
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commit() // commitAllowingStateLoss() -> commit() 으로 변경
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commit() // commitAllowingStateLoss() -> commit() 으로 변경
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commit() // commitAllowingStateLoss() -> commit() 으로 변경
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val jsonToSong = sharedPreferences.getString("songData", null)
//        Log.d("jsonToSong", jsonToSong.toString())
//        song = if (jsonToSong == null) { // 최초 실행 시
//            Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")
//        } else { // SongActivity에서 노래가 한번이라도 pause 된 경우
//            gson.fromJson(jsonToSong, Song::class.java)
//        }
        val spf=getSharedPreferences("song",MODE_PRIVATE)
        val songId =spf.getInt("songId",0)

        val songDB = SongDatabase.getInstance(this)!!

        song=if(songId==0){
            songDB.songDao().getSong(1)
        }
        else{
            songDB.songDao().getSong(songId)
        }

        Log.d("Song ID",song.id.toString())

        setMiniPlayer(song)
    }

    private fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniplayerProgressSb.progress = (song.second * 1000 / song.playTime)
    }

    fun updateMainPlayerCl(album : Album) {
        binding.mainMiniplayerTitleTv.text = album.title
        binding.mainMiniplayerSingerTv.text = album.singer
        binding.mainMiniplayerProgressSb.progress = 0
    }
    private fun inputDummySongs(){
        val songDB=SongDatabase.getInstance(this)
        val songs= songDB.songDao().getSongs()

        if(songs.isNotEmpty()) return
        songDB.songDao().insert(
            Song(
                title = "오래오래",
                singer = "George",
                second = 180,
                playTime = 180,
                isPlaying = false,
                music = "music_longlong",
                coverImg = R.drawable.img_album_exp,
                isLike = false
            )
        )
        songDB.songDao().insert(
            Song(
                title = "Lilac",
                singer = "아이유 (IU)",
                second = 180,
                playTime = 180,
                isPlaying = false,
                music = "music_lilac",
                coverImg = R.drawable.img_album_exp2,
                isLike = false
            )
        )
        songDB.songDao().insert(
            Song(
                title = "seasons",
                singer = "wave to earth",
                second = 180,
                playTime = 180,
                isPlaying = false,
                music = "music_seasons",
                coverImg = R.drawable.img_album_exp3,
                isLike = false
            )
        )
        songDB.songDao().insert(
            Song(
                title = "모스부호",
                singer = "dragon pony",
                second = 180,
                playTime = 180,
                isPlaying = false,
                music = "music_code",
                coverImg = R.drawable.img_album_exp4,
                isLike = false
            )
        )
        songDB.songDao().insert(
            Song(
                title = "summer",
                singer = "the volunteer",
                second = 180,
                playTime = 180,
                isPlaying = false,
                music = "music_summer",
                coverImg = R.drawable.img_album_exp5,
                isLike = false
            )
        )
        songDB.songDao().insert(
            Song(
                title = "Up!",
                singer = "Balming tiger",
                second = 180,
                playTime = 180,
                isPlaying = false,
                music = "music_up",
                coverImg = R.drawable.img_album_exp6,
                isLike = false
            )
        )

        val _songs=songDB.songDao().getSongs()
        Log.d("DB data",_songs.toString())
    }

}