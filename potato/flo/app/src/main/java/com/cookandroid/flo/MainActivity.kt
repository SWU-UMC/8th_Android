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


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var song : Song = Song()
    private var gson : Gson = Gson()

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

        initBottomNavigation()

        //val song = Song(
          //  binding.mainMiniplayerTitleTv.text.toString(),
           // binding.mainMiniplayerSingerTv.text.toString(),0,60,false, "music_lilac")  //음악 정보 담음.
            //5주차 수업 때, SongActivity에서 값을 가져오는 코드를 구현했기에 필요 없음

        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playtime)
            intent.putExtra("isplaying", song.isPlaying)
            intent.putExtra("music",song.music) //5주차 음악 정보 추가
            launcher.launch(intent)
        }

    }

    fun openAlbumFragment(albumTitle: String,singerName: String, albumImageResId: Int) {
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
     fun setMiniPlayer(song : Song){  //외부에서도 접근 가능하게 수정!
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second*100000)/song.playtime //시크바 최대 10만

        // 기존 재생 중 음악 정리
        mediaPlayer?.release()
        mediaPlayer = null

        // 음악 파일 재생
        //val resId = resources.getIdentifier(song.music, "raw", packageName)
        //mediaPlayer = MediaPlayer.create(this, resId)
        //mediaPlayer?.start()

        // 음악 파일 리소스 ID 얻기
        val resId = resources.getIdentifier(song.music, "raw", packageName)

        if (resId == 0) {
            Log.e("MediaPlayer", "음악 파일 리소스를 찾을 수 없습니다: ${song.music}")
            Toast.makeText(this, "음악 파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
            return
        }
        // mediaPlayer 생성 및 재생 시작
        mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer?.start()

        // 시크바 동기화 타이머 시작
        startMiniPlayerProgress()

        //버튼 UI 상태 초기화
        binding.mainMiniplayerBtn.visibility = View.GONE
        binding.mainPauseBtn.visibility = View.VISIBLE


        // 재생/일시정지 버튼 연결
        binding.mainMiniplayerBtn.setOnClickListener {
            mediaPlayer?.start()
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
        }

        binding.mainPauseBtn.setOnClickListener {
            mediaPlayer?.pause()
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
    override fun onStart(){
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) //데이터를 가지고 있는 이름! 
        val songJson = sharedPreferences.getString("songData", null) //진짜 데이터 가져오기

        //가져온 데이터를 송 객체에

        song = if(songJson == null) { //데이터 값이 없을 때 오류가 나지 않도록!
            Song("라일락", "아이유(IU)", 0 , 60,false, "music_lilac")
        } else{
            gson.fromJson(songJson, song::class.java)
        }

        setMiniPlayer(song)


    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null

        //타이머도 함께 정리
        miniPlayerTimer?.interrupt()
        miniPlayerTimer = null
    }



}