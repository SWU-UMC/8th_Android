package com.cookandroid.flo


//var = 변경 가능, val = 변경 불가
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.flo.databinding.ActivitySongBinding
import com.google.gson.Gson


class SongActivity : AppCompatActivity() {

    //소괄호는 클래스를 다른 클래스로 상속을 진행할 때 사용.

    //전역 변수(이거 자바에서.. 본 것 같은..)
    lateinit var binding : ActivitySongBinding
    lateinit var song : Song
    lateinit var timer : Timer

    //미디어 플레이어를 이용. 음악을 재생
    private var mediaPlayer : MediaPlayer? = null //음악 값이 널일 수 있기 때문.

    //Gson 소환!
    private var gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //데이터를 받아옴
        initSong()
        setPlayer(song)

//        if(intent.hasExtra("title") && intent.hasExtra("singer")){
//            binding.songMusicTitleTv.text = intent.getStringExtra("title")
//            binding.songSingerNameTv.text = intent.getStringExtra("singer")
//        }

        binding.songDownIb.setOnClickListener {
            //노래 이름, 가수 이름 정보를 메인 액티비티에 전달함.
            val resultIntent = Intent().apply {
                putExtra("albumTitle", intent.getStringExtra("title"))
                putExtra("singerName", intent.getStringExtra("singer"))
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }



//        binding.songMiniplayerIv.setOnClickListener {
//            setPlayerStatus(false)
//        }
//
//        binding.songPauseIv.setOnClickListener {
//            setPlayerStatus(true)
//        }

        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        /*if(intent.hasExtra("title") && intent.hasExtra("singer"))
            binding.songMusicTitleTv.text=intent.getStringExtra("title") //텍스트 뷰에서 텍스트를 바꿔줄건데..
            binding.songSingerNameTv.text=intent.getStringExtra("singer")*/
        //1곡 재생 버튼!
        binding.songRepeatIv.setOnClickListener {
            restartSong()
        }

    }
    //사용자가 포커스를 잃으면 음악 중지!
    override fun onPause(){
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.songProgressbarSb.progress * song.playtime)/100)/1000 //미니 플레이어에 기존에 재생되고 있던 쏭데이터를 반영!
        //1000으로 나누는 이유 -> 초 단위로 계산중!

        //데이터를 저장!
        //내부 저장시 앱에 저장할 수 있는것이 SharedPreference.(로그인 기억하기!)
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() //에디터가 필요함.
        //editor.putString("title", song.title) //song 정보를.. 다.. 하기 번거로워! json으로 데이터를 전송할 것이다!
        //자바 객체를 -> json => Gson! -> SharedPreference에 넣을 것이다
        val songJson = gson.toJson(song) //쥐슨이 json 포멧으로 변환
        editor.putString("songData", songJson)

        editor.apply() //실제 저장작업 완료! 꼭 있어야 해(커밋, 푸쉬랑 비슷한 개념)
        

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt() //쓰레드 종료시...
        //불필요한 리소스 방지!
        mediaPlayer?.release() //미디어 플에이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플에이어 해제


    }

    private fun initSong(){
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isplaying", false),
                intent.getStringExtra("music")!! //음악 정보 주고 받는...

            )
        }

        startTimer()
    }

    private fun restartSong() {
        timer.interrupt() // 현재 타이머 중지
        mediaPlayer?.seekTo(0) // 음악 다시 처음으로 돌아가야지!
        mediaPlayer?.start() // 음악 다시 재생

        song.second = 0 // 시작시간 기화
        setPlayerStatus(true)
        startTimer() // 새로운 타이머 다시 시작
    }

    private fun setPlayer(song: Song){


        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        //경고 메시지가 78번 줄에 떠서 찾아봤고, 해결방법은 locale.US를 추가해야함.
        //실행에는 오류가 없기에, 강의와 맞게 코드를 진행함.
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second/60, song.second %60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playtime/60, song.playtime %60)
        binding.songProgressbarSb.progress = (song.second * 1000 / song.playtime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName) //뮤직 리소스 받아옴.
        mediaPlayer = MediaPlayer.create(this,music) //이 음악을 재생할거야!

        setPlayerStatus(song.isPlaying)


    }

    fun setPlayerStatus (isPlaying : Boolean){
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        //재생을 눌렀을 때, 시크바가 멈추고, 멈춤을 눌렀을 때 시크바가 재생되기에 강의와 달리 순서를 바꿈!

//        if(isPlaying){
//            binding.songMiniplayerIv.visibility = View.VISIBLE //사용자가 제생/멈춤 버튼을 클릭했을 때, 달라짐.
//            binding.songPauseIv.visibility = View.GONE
//        } else {
//            binding.songMiniplayerIv.visibility = View.GONE
//            binding.songPauseIv.visibility = View.VISIBLE
//        }

        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.GONE  // ▶재생 버튼 숨김
            binding.songPauseIv.visibility = View.VISIBLE   // ⏸멈춤 버튼 표시
            mediaPlayer?.start()
        } else {
            binding.songMiniplayerIv.visibility = View.VISIBLE  // ▶재생 버튼 표시
            binding.songPauseIv.visibility = View.GONE         // ⏸멈춤 버튼 숨김
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause() //미디어 플에이어는 재생아닐때 멈춤을 하면 오류가 발생해서 이와 같은 if문을 사용함.
            }
        }
    }

    private fun startTimer(){
        timer = Timer(song.playtime, song.isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread(){

        private var second : Int = 0
        private var mills : Float = 0f

        override fun run(){
            super.run()
            try{
                while(true){
                    if(second >= playTime){
                        break
                    }
                    if (isPlaying){
                        sleep(50)
                        mills += 50



                        runOnUiThread{

                            binding.songProgressbarSb.progress = ((mills/playTime)*100).toInt()
                        }


                        //진행하는 타이머.
                        if(mills%1000 == 0f){
                            runOnUiThread{

                                binding.songStartTimeTv.text = String.format("%02d:%02d",second/60, second %60)
                            }
                            second++
                        }
                    }
                }
            }
            catch(e :InterruptedException){

                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            } //while문 오류 방지


        }

    }
}