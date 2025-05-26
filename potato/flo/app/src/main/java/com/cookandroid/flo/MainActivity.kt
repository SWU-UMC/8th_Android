package com.cookandroid.flo

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.cookandroid.flo.databinding.ActivityMainBinding
import com.google.gson.Gson
import android.os.Handler
import android.os.Looper



//Room 데이터 구조를 다시금 확인이 필요할 수도 있어서 전체 주석 처리후 작업.


class MainActivity : AppCompatActivity() {

    companion object {
        var sharedMediaPlayer: MediaPlayer? = null
        var currentSong: SaveSong? = null
    }

    lateinit var binding: ActivityMainBinding

    // val song: Song = Song()
    private var song: SaveSong = SaveSong()
    private var gson: Gson = Gson()

    private var mediaPlayer: MediaPlayer? = null
    private var miniPlayerTimer: Thread? = null

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val albumTitle = result.data?.getStringExtra("albumTitle") ?: ""
                val singerName = result.data?.getStringExtra("singerName") ?: ""
                if (albumTitle.isNotEmpty() && singerName.isNotEmpty()) {
                    Toast.makeText(this, "$albumTitle - $singerName", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Flo)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()
        inputDummyAlbums()

        initBottomNavigation()

        binding.mainPlayerCl.setOnClickListener {
            if (currentSong == null) {
                Log.e("MainActivity", "currentSong is NULL - 저장 불가")
                Toast.makeText(this, "노래가 재생되고 있지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putString("songData", gson.toJson(currentSong)) // ❗ 이게 null이면 songData도 null이 됨
            editor.putInt("songSecond", mediaPlayer?.currentPosition ?: 0)
            editor.putBoolean("songIsPlaying", mediaPlayer?.isPlaying == true)
            editor.apply()

            Log.d("MainActivity", "songData 저장 완료: ${gson.toJson(currentSong)}")

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
        Log.d("MAIN/JWT_TO_SERVER",getJwt().toString())
    }

    private fun getJwt(): Int{
        val spf = this.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

        return spf!!.getInt("jwt",0)
    }


    fun openAlbumFragment(albumTitle: String, singerName: String, albumImageResId: Int) {
        val albumFragment = AlbumFragment().apply {
            arguments = Bundle().apply {
                putString("albumTitle", albumTitle)
                putString("singerName", singerName)
                putInt("albumImageResId", albumImageResId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, albumFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    true
                }

                else -> false
            }
        }
    }

    fun setMiniPlayer(song: SaveSong) {
        Log.d("MainActivity", "🔊 setMiniPlayer() 호출됨: ${song.title}")
        Log.d("MiniPlayer", "setMiniPlayer 실행됨 - ${song.title}")
        binding.mainPlayerCl.visibility = View.VISIBLE  // ✅ 반드시 보여주기


        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second * 100000) / song.playtime

        mediaPlayer?.release()
        mediaPlayer = null

        val resId = resources.getIdentifier(song.music, "raw", packageName)

        if (resId == 0) {
            Log.e("MediaPlayer", "음악 파일 리소스를 찾을 수 없습니다: ${song.music}")
            Toast.makeText(this, "음악 파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayer = MediaPlayer.create(this, resId)

        if (mediaPlayer == null) {
            Log.e("MediaPlayer", "MediaPlayer 생성 실패")
            Toast.makeText(this, "음악을 재생할 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        sharedMediaPlayer = mediaPlayer
        currentSong = song

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songSecond = spf.getInt("songSecond", 0)
        val isPlaying = spf.getBoolean("songIsPlaying", false)

        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer?.seekTo(songSecond)

            if (isPlaying) {
                mediaPlayer?.start()
                startMiniPlayerProgress()
                binding.mainMiniplayerBtn.visibility = View.GONE
                binding.mainPauseBtn.visibility = View.VISIBLE
            } else {
                binding.mainMiniplayerBtn.visibility = View.VISIBLE
                binding.mainPauseBtn.visibility = View.GONE
            }
        }, 500)

        binding.mainMiniplayerBtn.setOnClickListener {
            mediaPlayer?.start()
            startMiniPlayerProgress()
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
        }

        binding.mainPauseBtn.setOnClickListener {
            mediaPlayer?.pause()
            binding.mainPauseBtn.visibility = View.GONE
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
        }

        mediaPlayer?.setOnCompletionListener {
            binding.mainPauseBtn.visibility = View.GONE
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
        }
    }

    private fun startMiniPlayerProgress() {
        miniPlayerTimer?.interrupt()
        miniPlayerTimer = object : Thread() {
            override fun run() {
                try {
                    while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        val current = mediaPlayer!!.currentPosition
                        val total = mediaPlayer!!.duration
                        runOnUiThread {
                            binding.mainProgressSb.progress = (current * 100000) / total
                        }
                        sleep(500)
                    }
                } catch (e: InterruptedException) {
                    Log.d("MiniPlayer", "타이머 쓰레드 중지됨: ${e.message}")
                }
            }
        }
        miniPlayerTimer?.start()
    }

    override fun onStart() {
        super.onStart()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = spf.getString("songData", null)
        val songSecond = spf.getInt("songSecond", 0)
        val isPlaying = spf.getBoolean("songIsPlaying", false)

        if (songJson != null) {
            val loadedSong = try {
                gson.fromJson(songJson, SaveSong::class.java)
            } catch (e: Exception) {
                Log.e("MainActivity", "Gson 변환 실패: ${e.message}")
                null
            }

            if (loadedSong != null) {
                loadedSong.second = songSecond / 1000
                loadedSong.isPlaying = isPlaying
                setMiniPlayer(loadedSong)
            } else {
                Toast.makeText(this, "노래 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "저장된 노래가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedMediaPlayer?.release()
        mediaPlayer = null
        sharedMediaPlayer = null
        miniPlayerTimer?.interrupt()
        miniPlayerTimer = null
    }

    // RoomDB용 더미 데이터 삽입 함수 (현재 사용 안 함)
    /*
    private fun inputDummySongs() { ... }
    private fun insertDummySongs(songDB: SongDatabase) { ... }
    */

    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return
        songDB.songDao().insert(
            Song("LiLac", "IU", 0, 240, 1, false, "music_lilac", R.drawable.img_album_exp2, false)
        )
        songDB.songDao().insert(
            Song(
                "See Me gwisun",
                "Daeseong",
                0,
                240,
                2,
                false,
                "music_seeme",
                R.drawable.see_me,
                false
            )
        )
        songDB.songDao().insert(
            Song("Sign", "Izna", 0, 240, 3, false, "music_sign", R.drawable.izna_sign, false)
        )
        songDB.songDao().insert(
            Song(
                "Like Jennie",
                "Jennie",
                0,
                240,
                4,
                false,
                "music_likejennie",
                R.drawable.jennie_like_jennie,
                false
            )
        )
        songDB.songDao().insert(
            Song(
                "Whiplash",
                "Aespa",
                0,
                240,
                5,
                false,
                "music_whiplash",
                R.drawable.aespa_whiplash,
                false
            )
        )
        songDB.songDao().insert(
            Song(
                "Extral",
                "Jennie",
                0,
                240,
                6,
                false,
                "music_extral",
                R.drawable.jennie_extral,
                false
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("insertDummySongs", "삽입 완료: $_songs")
    }

    //ROOM_DB
    private fun inputDummyAlbums() {
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                0,
                "IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )
        songDB.albumDao().insert(
            Album(
                1,
                "See Me gwisun","Daeseong",R.drawable.see_me
            )
        )
        songDB.albumDao().insert(
            Album(
                2,
                "Sign","Izna",R.drawable.izna_sign
            )
        )
        songDB.albumDao().insert(
            Album(
                3,
                "Like Jennie","Jennie",R.drawable.jennie_like_jennie

            )
        )
        songDB.albumDao().insert(
            Album(
                4,
                "Whiplash","Aespa",R.drawable.aespa_whiplash

            )
        )
        songDB.albumDao().insert(
            Album(
                5,
                "Extral", "Jennie", R.drawable.jennie_extral
            )
        )

    }
}
/*class MainActivity : AppCompatActivity() {

    companion object {
        var sharedMediaPlayer: MediaPlayer? = null //SongActivity와 공유할 MediaPlayer
        var currentSong: Song? = null //Song 공유된 현재 Song
    }

    lateinit var binding: ActivityMainBinding

    private var song: Song = Song() //shardPrefersence를 통해  id를 받아옴
    private var gson: Gson = Gson() //PR작성용 주석

    private var mediaPlayer: MediaPlayer? = null //음악 재생 추가
    private var miniPlayerTimer: Thread? = null //미니 플레이어 타이머



    //registerForActivityResult 이용해서, songActivity에서 토스트 띄우기를 위한... 코드!(송 액티비티로 부터, 제목 - 가수 정보를 받아옴.)
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val albumTitle = result.data?.getStringExtra("albumTitle") ?: ""
                val singerName = result.data?.getStringExtra("singerName") ?: ""
                if (albumTitle.isNotEmpty() && singerName.isNotEmpty()) {
                    Toast.makeText(this, "$albumTitle - $singerName", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Flo)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()

        initBottomNavigation()

        //val song = Song(
        //  binding.mainMiniplayerTitleTv.text.toString(),
        // binding.mainMiniplayerSingerTv.text.toString(),0,60,false, "music_lilac")  //음악 정보 담음.
        //5주차 수업 때, SongActivity에서 값을 가져오는 코드를 구현했기에 필요 없음

        binding.mainPlayerCl.setOnClickListener {
//            val intent = Intent(this, SongActivity::class.java)
//            intent.putExtra("title", song.title)
//            intent.putExtra("singer", song.singer)
//            intent.putExtra("second", song.second)
//            intent.putExtra("playTime", song.playtime)
//            intent.putExtra("isplaying", song.isPlaying)
//            intent.putExtra("music",song.music) //5주차 음악 정보 추가
//            launcher.launch(intent)
           /* val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)*/
            /*val songJson = gson.toJson(currentSong)
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putString("songData", songJson)
            editor.putInt("songId", currentSong?.id ?: 0)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)*/

            //계속 이미지 전달에 오류가 있어 부득이하게 gpt 도움을 받았습니다...
            // 🎯 songId만 저장 (JSON 저장 X)
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", currentSong?.id ?: 0)
            editor.apply()

            // 🎯 SongActivity에서는 DB에서 이 ID로 Song을 불러오게 하기
            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)

        }
    }


    fun openAlbumFragment(albumTitle: String, singerName: String, albumImageResId: Int) {
        val albumFragment = AlbumFragment().apply {
            arguments = Bundle().apply {
                putString("albumTitle", albumTitle)
                putString("singerName", singerName)
                putInt("albumImageResId", albumImageResId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, albumFragment)
            .addToBackStack(null) //
            // 뒤로 가기 버튼 누르면 이전 화면으로 돌아가기 가능
            .commit()
    }


    private fun initBottomNavigation() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
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

    //미니 플레이어에 반영하는 함수!
    fun setMiniPlayer(song: Song) {  //외부에서도 접근 가능하게 수정!
    //fun setMiniPlayer(song: Song, isPlaying: Boolean, songSecond: Int){
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second * 100000) / song.playtime //시크바 최대 10만

        // 기존 재생 중 음악 정리
        mediaPlayer?.release()
        mediaPlayer = null

        // 음악 파일 재생
        //val resId = resources.getIdentifier(song.music, "raw", packageName)
        //mediaPlayer = MediaPlayer.create(this, resId)
        //mediaPlayer?.start()
        Log.d("setMiniPlayer", "선택된 곡: ${song.title}, 파일명: ${song.music}")

        // 음악 파일 리소스 ID 얻기
        val resId = resources.getIdentifier(song.music, "raw", packageName)

        if (resId == 0) {
            Log.e("MediaPlayer", "음악 파일 리소스를 찾을 수 없습니다: ${song.music}")
            Toast.makeText(this, "음악 파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
            return
        }
         //mediaPlayer 생성 및 재생 시작
        mediaPlayer = MediaPlayer.create(this, resId) //MediaPlayer 생성
       // mediaPlayer?.start() //수정. 미니플레이어 겹침 방지
        if (mediaPlayer == null) {
            Log.e("MediaPlayer", "MediaPlayer 생성 실패")
            Toast.makeText(this, "음악을 재생할 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 공유 변수로 저장
        sharedMediaPlayer = mediaPlayer //공유 객체에 할당
        currentSong = song //현재 곡도 공유


//        mediaPlayer?.start() //바로 재생 시작
//        // 시크바 동기화 타이머 시작
//        startMiniPlayerProgress()

        // ✅ SharedPreferences에서 재생 상태 및 위치 로드
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songSecond = spf.getInt("songSecond", 0)
        val isPlaying = spf.getBoolean("songIsPlaying", false)

        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer?.seekTo(songSecond)

            if (isPlaying) {
                mediaPlayer?.start()
                startMiniPlayerProgress()
                binding.mainMiniplayerBtn.visibility = View.GONE
                binding.mainPauseBtn.visibility = View.VISIBLE
            } else {
                binding.mainMiniplayerBtn.visibility = View.VISIBLE
                binding.mainPauseBtn.visibility = View.GONE
            }

        }, 500) // 0.5초 지연 //애뮬레이터 성능의 한계 이슈발생.


        // 재생/일시정지 버튼 연결
        binding.mainMiniplayerBtn.setOnClickListener {
            mediaPlayer?.start() //공유 객체 재생
            startMiniPlayerProgress()
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
        }

        binding.mainPauseBtn.setOnClickListener {
            mediaPlayer?.pause()  //공유 객체 일시정지
            binding.mainPauseBtn.visibility = View.GONE
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
        }

        // 음악이 끝났을 때 처리
        mediaPlayer?.setOnCompletionListener {
            binding.mainPauseBtn.visibility = View.GONE
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
        }



    }

    private fun startMiniPlayerProgress() {


        miniPlayerTimer?.interrupt()
        miniPlayerTimer = object : Thread() {
            override fun run() {
                try {
                    while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        val current = mediaPlayer!!.currentPosition
                        val total = mediaPlayer!!.duration
                        runOnUiThread {
                            binding.mainProgressSb.progress = (current * 100000) / total
                        }
                        sleep(500)
                    }
                } catch (e: InterruptedException) {
                    Log.d("MiniPlayer", "타이머 쓰레드 중지됨: ${e.message}")
                }
            }
        }
        miniPlayerTimer?.start()
    }


    //액티비티 전환할 때 부터, onStart() 시작되는 것이기에! SongActivity내용을 받아올 것이기에!
    override fun onStart() {
        super.onStart()
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) //데이터를 가지고 있는 이름!
//        val songJson = sharedPreferences.getString("songData", null) //진짜 데이터 가져오기
//
//        //가져온 데이터를 송 객체에
//
//        song = if(songJson == null) { //데이터 값이 없을 때 오류가 나지 않도록!
//            Song("라일락", "아이유(IU)", 0 , 60,false, "music_lilac")
//        } else{
//            gson.fromJson(songJson, song::class.java)
//        }
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        //val songId = spf.getInt("songDB", 0)
        val songId = spf.getInt("songId", 1)
        val songSecond = spf.getInt("songSecond", 0)
        val isPlaying = spf.getBoolean("songIsPlaying", false) //재생 여부.
        //val songId = spf.getInt("songId", 0)
        val songDB = SongDatabase.getInstance(this)!!
        val dbSong = songDB.songDao().getSong(songId)

        if (dbSong != null) {
            song = dbSong
            song.second = songSecond / 1000
            song.isPlaying = isPlaying
            Log.d("song ID", song.id.toString())
            setMiniPlayer(song)
        } else {
            Log.e("onStart", "DB에서 ID=${songId}인 노래를 찾을 수 없습니다.")
            Toast.makeText(this, "저장된 노래가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }


//        song = songDB.songDao().getSong(songId)
//        song.second = songSecond / 1000 // 초 단위로 변환 (UI용)
//        song.isPlaying = isPlaying


//        song = if (songId == 0) {
//            songDB.songDao().getSong(1)
//        } else {
//            songDB.songDao().getSong(songId)
//        }
//        Log.d("song ID", song.id.toString())
//        setMiniPlayer(song)
//        //db에서 해당하는 id의 노래를 가져와야 함.


   // }

    override fun onDestroy() {
        super.onDestroy()
       // mediaPlayer?.release()
        sharedMediaPlayer?.release()
        mediaPlayer = null
        sharedMediaPlayer = null

        //타이머도 함께 정리
        miniPlayerTimer?.interrupt()
        miniPlayerTimer = null
    }

    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)
        try {
            val songs = songDB.songDao().getSongs()

            if (songs.isEmpty()) {
                Log.d("inputDummySongs", "DB 비어있음 → 더미 삽입 시작")
                insertDummySongs(songDB) // ✅ 별도 함수 호출
                return
            }

            Log.d("inputDummySongs", "DB에 이미 ${songs.size}곡 존재함 → 삽입 생략")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("inputDummySongs", "DB 오류: ${e.localizedMessage}")
        }
    }
    private fun insertDummySongs(songDB: SongDatabase) {
        songDB.songDao().insert(
            Song("LiLac", "IU", 0, 240, 1, false, "music_lilac", R.drawable.img_album_exp2, false)
        )
        songDB.songDao().insert(
            Song("See Me gwisun", "Daeseong", 0, 240, 2, false, "music_seeme", R.drawable.see_me, false)
        )
        songDB.songDao().insert(
            Song("Sign", "Izna", 0, 240, 3, false, "music_sign", R.drawable.izna_sign, false)
        )
        songDB.songDao().insert(
            Song("Like Jennie", "Jennie", 0, 240, 4, false, "music_likejennie", R.drawable.jennie_like_jennie, false)
        )
        songDB.songDao().insert(
            Song("Whiplash", "Aespa", 0, 240, 5, false, "music_whiplash", R.drawable.aespa_whiplash, false)
        )
        songDB.songDao().insert(
            Song("Extral", "Jennie", 0, 240, 6, false, "music_extral", R.drawable.jennie_extral, false)
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("insertDummySongs", "삽입 완료: $_songs")
    }
}


        //데이터가 없다면 더미데이터를 넣어야 함.
       //  if (songs.isNotEmpty()) return
/*
        songDB.songDao().insert(
            Song(
                "LiLac", //title
                "IU", //singer
                0,
                240,
                1,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false

            )
        )

        songDB.songDao().insert(
            Song(
                "See Me gwisun", //title
                "Daeseong", //singer
                0,
                240,
                2,
                false,
                "music_seeme",
                R.drawable.see_me,
                false

            )
        )

        songDB.songDao().insert(
            Song(
                "Sign", //title
                "Izna", //singer
                0,
                240,
                3,
                false,
                "music_sign",
                R.drawable.izna_sign,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "Like Jennie", //title
                "Jennie", //singer
                0,
                240,
                4,
                false,
                "music_likejennie",
                R.drawable.jennie_like_jennie,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "Whiplash", //title
                "Aespa", //singer
                0,
                240,
                5,
                false,
                "music_whiplash",
                R.drawable.aespa_whiplash,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "Extral", //title
                "Jennie", //singer
                0,
                240,
                6,
                false,
                "music_extral",
                R.drawable.jennie_extral,
                false
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }
}

*/