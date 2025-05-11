package com.cookandroid.flo


//var = 변경 가능, val = 변경 불가
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.flo.databinding.ActivitySongBinding
import com.google.gson.Gson


class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer

    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0
    private var isOneRepeatOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()

        val player = MainActivity.sharedMediaPlayer
        val song = MainActivity.currentSong

        if (player == null || song == null) {
            finish()
            return
        }

        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playtime / 60, song.playtime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg ?: R.drawable.img_album_exp2)

        // 버튼 초기 상태 설정
        setPlayerStatus(player.isPlaying) // 수정됨

        binding.songDownIb.setOnClickListener {
            // 재생 위치 및 상태 저장 후 종료 처리 - 수정됨
            savePlaybackState()
            val resultIntent = Intent().apply {
                putExtra("albumTitle", song.title)
                putExtra("singerName", song.singer)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }




    }

    override fun onPause() {
        super.onPause()
        savePlaybackState() // 수정됨
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener{
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener{
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener{
            setPlayerStatus(false)
        }
        binding.songNextIv.setOnClickListener{
            moveSong(+1)
        }
        binding.songPreviousIv.setOnClickListener{
            moveSong(-1)
        }
        binding.songRepeatIv.setOnClickListener {
            isOneRepeatOn = !isOneRepeatOn
            Toast.makeText(this, if (isOneRepeatOn) "한 곡 반복 ON" else "한 곡 반복 OFF", Toast.LENGTH_SHORT).show()
        }

    }
    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        if (songs.isEmpty()) {
            Log.e("initSong", "songs 리스트가 비어 있습니다.")
            finish()
            return
        }

        nowPos = getPlayingSongPosition(songId)

        if (nowPos !in songs.indices) {
            Log.e("initSong", "잘못된 nowPos: $nowPos")
            finish()
            return
        }

        setPlayer(songs[nowPos])
    }

    private fun moveSong(direct: Int) {
        //이전 누르면 -1, 다음 +1
        /*if(nowPos * direct < 0 ){
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT)
            return
        }
        if(nowPos * direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }
        nowPos += direct

        timer.interrupt()
        // 기존 공유 MediaPlayer 정리
        MainActivity.sharedMediaPlayer?.release()

        // 새로운 곡으로 MediaPlayer 설정 (중요!!)
        val newSong = songs[nowPos]
        val musicRes = resources.getIdentifier(newSong.music, "raw", packageName)
        MainActivity.sharedMediaPlayer = MediaPlayer.create(this, musicRes)
        MainActivity.currentSong = newSong

        // 새로 시작
        MainActivity.sharedMediaPlayer?.start()

        setPlayer(newSong)*/
        val newPos = nowPos + direct

        if (newPos < 0) {
            Toast.makeText(this, "첫 번째 곡입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPos >= songs.size) {
            Toast.makeText(this, "마지막 곡입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos = newPos
        timer.interrupt()

        val newSong = songs[nowPos]
        val musicResId = resources.getIdentifier(newSong.music, "raw", packageName)
        val afd = resources.openRawResourceFd(musicResId)

        try {
            MainActivity.sharedMediaPlayer?.apply {
                reset() // 플레이어 초기화
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }
            MainActivity.currentSong = newSong
            setPlayer(newSong)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "음악을 재생할 수 없습니다.", Toast.LENGTH_SHORT).show()
        } finally {
            afd.close()
        }
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) return i
        }
        return 0
    }

    private fun restartSong() {
        timer.interrupt()
        mediaPlayer?.seekTo(0)
        MainActivity.sharedMediaPlayer?.start()
        songs[nowPos].second = 0
        setPlayerStatus(true)
        startTimer()
    }

    private fun setPlayer(song: Song) {
        // 새로 바뀌었으므로 항상 0으로 시작
        val currentPosition = 0

        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songAlbumIv.setImageResource(song.coverImg ?: R.drawable.img_album_exp2)

        binding.songStartTimeTv.text = "00:00"
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playtime / 60, song.playtime % 60)

        binding.songProgressbarSb.progress = 0  // 명확하게 0에서 시작

        startTimer()
        setPlayerStatus(MainActivity.sharedMediaPlayer?.isPlaying == true)
    }

    private fun togglePlayerStatus() { // 재생/멈춤 토글 함수 - 수정됨
        val player = MainActivity.sharedMediaPlayer ?: return
        if (player.isPlaying) {
            setPlayerStatus(false)
        } else {
            setPlayerStatus(true)
        }
    }

    fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        val player = MainActivity.sharedMediaPlayer ?: return

        if (isPlaying) {
            if (!player.isPlaying) player.start()
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        } else {
            if (player.isPlaying) player.pause()
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
    }

    private fun savePlaybackState() { // 재생 위치와 상태 저장 함수 - 수정됨
        val player = MainActivity.sharedMediaPlayer
        val currentPosition = player?.currentPosition ?: 0

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("songSecond", currentPosition)
        editor.putBoolean("songIsPlaying", player?.isPlaying == true)
        editor.apply()
    }

    private fun startTimer() {
        timer = Timer(songs[nowPos].playtime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second: Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (second >= playTime) {
                        if (isOneRepeatOn) {
                            second = 0
                            mills = 0f
                            runOnUiThread {
                                mediaPlayer?.seekTo(0)
                                mediaPlayer?.start()
                                setPlayerStatus(true)
                            }
                        } else break
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50
                        runOnUiThread {
                            binding.songProgressbarSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}