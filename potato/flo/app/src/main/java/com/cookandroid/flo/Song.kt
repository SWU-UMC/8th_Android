package com.cookandroid.flo

import androidx.room.Entity
import androidx.room.PrimaryKey

//제목, 가수, 사진,재생시간,현재 재생시간, isplaying(재생 되고 있는지)

@Entity(tableName = "SongTable")
data class Song(
    var title : String = "",
    var singer : String = "",
    var second : Int = 0,
    var playtime : Int = 0,
    val albumIdx: Int = 0 , //추가
    var isPlaying : Boolean = false,
    //어떤 음악이 재생되는지를 알려줌
    var music: String = "",

    //7주차 데이터 베이스 사용을 위한 코드 추가
    var coverImg: Int? =  R.drawable.img_album_exp2, //null 방지 + 기본값 지정
    var isLike: Boolean = false

){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}