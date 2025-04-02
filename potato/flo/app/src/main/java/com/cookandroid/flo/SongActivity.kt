package com.cookandroid.flo


//var = 변경 가능, val = 변경 불가
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.flo.databinding.ActivitySongBinding


class SongActivity : AppCompatActivity() {

    //소괄호는 클래스를 다른 클래스로 상속을 진행할 때 사용.

    //전역 변수(이거 자바에서.. 본 것 같은..)
    lateinit var binding : ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }

        binding.songDownIb.setOnClickListener {
            //노래 이름, 가수 이름 정보를 메인 액티비티에 전달함.
            val resultIntent = Intent().apply {
                putExtra("albumTitle", intent.getStringExtra("title"))
                putExtra("singerName", intent.getStringExtra("singer"))
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }



        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(true)
        }

        /*if(intent.hasExtra("title") && intent.hasExtra("singer"))
            binding.songMusicTitleTv.text=intent.getStringExtra("title") //텍스트 뷰에서 텍스트를 바꿔줄건데..
            binding.songSingerNameTv.text=intent.getStringExtra("singer")*/


    }

    fun setPlayerStatus (isPlaying : Boolean){
        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.VISIBLE //사용자가 제생/멈춤 버튼을 클릭했을 때, 달라짐.
            binding.songPauseIv.visibility = View.GONE
        } else {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
    }
}