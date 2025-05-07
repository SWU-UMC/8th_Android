package com.example.a3week

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.a3week.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer

    private var mediaPlayer: MediaPlayer? = null
    private val gson: Gson = Gson() // 'val'로 선언되어 재할당 불가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songRepeatIv.setOnClickListener {
            mediaPlayer?.seekTo(0)
            timer.restartTimer() // Timer 재시작
            setPlayerStatus(true) // 재생 상태로 변경 (필요한 경우)
        }
    }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)

        // 'val'로 선언된 song 객체의 내부 프로퍼티를 변경
        song = song.copy(second = (song.playTime * binding.songProgressSb.progress) / 100000)
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songToJson = gson.toJson(song)
        editor.putString("songData", songToJson)
        Log.d("songData", songToJson.toString())
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() //미디어 플레이어가 가지고 있는 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제
    }

    private fun initSong() {
        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
            Log.d("SongActivity", "Intent Music Extra: ${intent.getStringExtra("music")}") // 추가된 로그
            Log.d("SongActivity", "Song Music Value: ${song.music}") // 추가된 로그
        }
        startTimer()
    }

    private fun setPlayer(song: Song) {
        Log.d("SongActivity", "setPlayer() 호출됨 - song.music 값: ${song.music}")
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        Log.d("SongActivity", "Music Resource ID: $music")
        mediaPlayer = MediaPlayer.create(this, music)
        Log.d("SongActivity", "MediaPlayer 객체: $mediaPlayer")
        if (mediaPlayer == null) {
            Log.e("SongActivity", "MediaPlayer 생성 실패!")
            // MediaPlayer 생성 실패 시 적절한 오류 처리
            return
        }
        setPlayerStatus(song.isPlaying)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start() //재생중일때
        } else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            } //일시정지했을때
        }
    }

    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {

        private var second: Int = 0
        private var mills: Float = 0f
        @Volatile
        private var isRunning = false;

        fun restartTimer() {
            if (!isRunning) { // 스레드가 실행 중이 아니면 새로 시작
                isRunning = true
                second = 0
                mills = 0f
                start()
            } else { // 스레드가 실행 중이면 상태만 초기화
                second = 0
                mills = 0f
            }
            isPlaying = true // 재시작 시 재생 상태로 설정
        }

        override fun run() {
            try {
                isRunning = true // 스레드 시작 시 실행 상태를 true로 설정
                while (isRunning && second <= playTime) { // isRunning 상태를 체크
                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                            binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                        }

                        if (mills % 1000 == 0f) {
                            second++
                        }
                    } else {
                        sleep(100) // 일시 정지 상태
                    }
                }
                isRunning = false // 스레드 종료 시 실행 상태를 false로 설정
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}