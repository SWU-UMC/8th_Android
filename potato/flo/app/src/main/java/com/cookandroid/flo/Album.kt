package com.cookandroid.flo


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey val id: Int,
    var title: String = "",
    var singer: String = "",
    var coverImg: Int? = null,
    val music: String = ""
) {
    @Ignore
    var songs: ArrayList<Song>? = null  // 좋아요랑 무관한 내부용 필드
}