package com.cookandroid.flo


//var = 변경 가능, val = 변경 불가
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.cookandroid.flo.databinding.ActivitySongBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import java.util.Timer


//혹시 모를 Room 데이터베이스 다시 살펴봐야할 수 있어서 기존 코드는 전체주석처리함.
class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer

    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    // ✅ RoomDB 사용 시
    // val songs = arrayListOf<Song>()
    // lateinit var songDB: SongDatabase

    // ✅ Firebase 기반
    val songs = arrayListOf<SaveSong>()

    var nowPos = 0
    private var isOneRepeatOn = false

    // ✅ Firebase 필드 선언
    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser"

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
        binding.songAlbumIv.setImageResource(song.coverImg)

        setPlayerStatus(player.isPlaying)

        binding.songDownIb.setOnClickListener {
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
        savePlaybackState()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initPlayList() {
        // ✅ Room 사용 시
        // songDB = SongDatabase.getInstance(this)!!
        // songs.addAll(songDB.songDao().getSongs())
    }

    private fun showLikePopup(isLiked: Boolean) {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.like_popup, null)
        val heartIv = view.findViewById<ImageView>(R.id.like_popup_heart_iv)
        val textTv = view.findViewById<TextView>(R.id.like_popup_text_tv)

        heartIv.setImageResource(if (isLiked) R.drawable.ic_my_like_on else R.drawable.ic_my_like_off)
        textTv.text = if (isLiked) "좋아요를 눌렀어요" else "좋아요를 취소했어요"

        val anim = AnimationUtils.loadAnimation(
            this,
            if (isLiked) R.anim.like_popup_anim else R.anim.unlike_popup_anim
        )
        heartIv.startAnimation(anim)

        val toast = Toast(this)
        toast.view = view
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
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

    private fun savePlaybackState() {
        val player = MainActivity.sharedMediaPlayer
        val currentPosition = player?.currentPosition ?: 0

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("songSecond", currentPosition)
        editor.putBoolean("songIsPlaying", player?.isPlaying == true)
        editor.apply()
    }

    private fun moveSong(direct: Int) {
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
                reset()
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

    private fun setPlayer(song: SaveSong) {
        val currentPosition = 0

        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = "00:00"
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playtime / 60, song.playtime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg)

        binding.songProgressbarSb.progress = 0
        startTimer()
        setPlayerStatus(MainActivity.sharedMediaPlayer?.isPlaying == true)

        if (song.isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
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

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }
        binding.songRepeatIv.setOnClickListener {
            isOneRepeatOn = !isOneRepeatOn
            Toast.makeText(this, if (isOneRepeatOn) "한 곡 반복 ON" else "한 곡 반복 OFF", Toast.LENGTH_SHORT).show()
        }
        binding.songLikeIv.setOnClickListener {
            val isNowLiked = songs[nowPos].isLike
            setLike(isNowLiked)
            showLikePopup(!isNowLiked)
        }
    }

    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = spf.getString("songData", null)

        if (songJson == null) {
            Toast.makeText(this, "노래 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val songFromJson = gson.fromJson(songJson, SaveSong::class.java)
        songs.add(songFromJson)
        nowPos = 0
        setPlayer(songFromJson)
    }

    // ✅ Firebase 기반 좋아요 저장/삭제 함수
    private fun setLike(isLike: Boolean) {
        val song = songs[nowPos]
        val newState = !isLike
        song.isLike = newState

        val ref = database.getReference("users/$userId/likedSongs/${song.id}")
        if (newState) {
            ref.setValue(song)
        } else {
            ref.removeValue()
        }

        if (newState) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    // moveSong, setPlayer, setPlayerStatus, savePlaybackState, startTimer 등은 그대로 유지
}
/*
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

    //좋아요시 나오는, 토스트 메시지 구현.

    private fun showLikePopup(isLiked: Boolean) {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.like_popup, null)
        val heartIv = view.findViewById<ImageView>(R.id.like_popup_heart_iv)
        val textTv = view.findViewById<TextView>(R.id.like_popup_text_tv)
        // 하트 이미지 설정
        heartIv.setImageResource(if (isLiked) R.drawable.ic_my_like_on else R.drawable.ic_my_like_off)

        // 텍스트 메시지 설정
        textTv.text = if (isLiked) "좋아요를 눌렀어요" else "좋아요를 취소했어요"

        // 애니메이션 설정
        val anim = AnimationUtils.loadAnimation(
            this,
            if (isLiked) R.anim.like_popup_anim else R.anim.unlike_popup_anim
        )
        heartIv.startAnimation(anim)

        val toast = Toast(this)
        toast.view = view
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
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
        binding.songLikeIv.setOnClickListener{
            //setLike(songs[nowPos].isLike)
            val isNowLiked = songs[nowPos].isLike
            setLike(isNowLiked)
            showLikePopup(!isNowLiked)  // 좋아요 또는 취소 애니메이션 실행

        }

    }
    private fun initSong() {
        /*val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = spf.getString("songData", null)
        val songId = spf.getInt("songId", 0)

        if (songJson == null) {
            Toast.makeText(this, "노래 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
            finish()

            return
        }
        val songFromJson = gson.fromJson(songJson, Song::class.java)

        Log.d("SongCheck", "songFromJson.title = ${songFromJson.title}, coverImg = ${songFromJson.coverImg}")
        nowPos = getPlayingSongPosition(songId)

        if (nowPos !in songs.indices) {
            Log.e("initSong", "잘못된 nowPos: $nowPos")
            finish()
            return
        }

        val updatedSong = songDB.songDao().getSong(songId)
        /*if (updatedSong != null) {
            songs[nowPos] = updatedSong
            setPlayer(songs[nowPos])
        } else {
            Log.e("initSong", "ID=${songId}인 노래를 DB에서 찾을 수 없습니다.")
            finish()
        }*/

        // ✅ 불러온 곡으로 덮어쓰기 (UI 반영용)
        songs[nowPos] = songFromJson
        setPlayer(songs[nowPos])

        //DB에서 최신 값 가져와 덮어쓰기
        //songs[nowPos] = songDB.songDao().getSong(songId)

        //setPlayer(songs[nowPos])*/

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = spf.getString("songData", null)

        if (songJson == null) {
            Toast.makeText(this, "노래 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val songFromJson = gson.fromJson(songJson, Song::class.java)
        Log.d("SongCheck", "songFromJson.title = ${songFromJson.title}, coverImg = ${songFromJson.coverImg}")
        Log.d("SongCheck", "resourceName = ${try { resources.getResourceName(songFromJson.coverImg!!) } catch (e: Exception) { "INVALID" }}")

        // 미니 플레이어에서 -> 송으로 사진 전달이 잘 안되어 수정.
        setPlayer(songFromJson)
    }

    private fun setLike(isLike: Boolean ){
        songs[nowPos].isLike = isLike //아직 디비 값 업데이트 전

        val newState = !isLike
        //DB 업데이트
        songDB.songDao().updateIsLikeById(newState, songs[nowPos].id)

        //메모리 상태도 동기화
        songs[nowPos].isLike = newState

        // UI 반영
        if (newState) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
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
        // binding.songAlbumIv.setImageResource(song.coverImg ?: R.drawable.img_album_exp2)

        val imgRes = song.coverImg ?: R.drawable.img_album_exp2
        binding.songAlbumIv.setImageResource(imgRes)

        binding.songStartTimeTv.text = "00:00"
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playtime / 60, song.playtime % 60)

        binding.songProgressbarSb.progress = 0  // 명확하게 0에서 시작

        startTimer()
        setPlayerStatus(MainActivity.sharedMediaPlayer?.isPlaying == true)

        if(song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }
        else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

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
class MainActivity : AppCompatActivity() {

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
}  */