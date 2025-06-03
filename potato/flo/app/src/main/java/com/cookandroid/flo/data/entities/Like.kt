package com.cookandroid.flo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LikeTable")
data class Like(
    var userId: Int, //앨범 id
    var albumId: Int) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}