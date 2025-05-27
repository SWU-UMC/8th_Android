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
    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

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
            if (songs.isEmpty()) return@setOnClickListener
            val editor=getSharedPreferences("song",MODE_PRIVATE).edit()
            editor.putInt("songId",songs[nowPos].id)
            editor.apply()

            val intent=Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
        inputDummySongs()
        initPlayList()
        initBottomNavigation()

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())
    }
    private fun getJwt(): String? {
        val spf = this.getSharedPreferences("auth2" , AppCompatActivity.MODE_PRIVATE)

        return spf!!.getString("jwt", "")
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

    override fun onResume() {
        super.onResume()

        if (songs.isEmpty()) return

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        setMiniPlayer(songs[nowPos])
    }
    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    fun setMiniPlayer(song : Song) {
        if (songs.isEmpty()) return
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        Log.d("songInfo", song.toString())
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val second = sharedPreferences.getInt("second", 0)
        Log.d("spfSecond", second.toString())
        binding.mainMiniplayerProgressSb.progress = (second * 100000 / song.playTime)
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
                isLike = false,
                1
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
                isLike = false,
                2
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
                isLike = false,
                3
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
                isLike = false,
                4
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
                isLike = false,
                5
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
                isLike = false,
                6
            )
        )

        val _songs=songDB.songDao().getSongs()
        Log.d("DB data",_songs.toString())
    }

}