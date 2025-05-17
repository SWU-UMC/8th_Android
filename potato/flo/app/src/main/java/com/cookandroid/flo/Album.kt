package com.cookandroid.flo


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey(autoGenerate = false) var id: Int = 0,
    val title: String = "",
    val singer: String = "",
    val coverImg: Int = 0,
    val music: String = ""
) {
    //@Ignore
    //var songs: ArrayList<Song>? = null  // 좋아요랑 무관한 내부용 필드
}