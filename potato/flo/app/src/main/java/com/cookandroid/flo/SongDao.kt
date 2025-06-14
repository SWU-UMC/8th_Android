package com.cookandroid.flo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cookandroid.flo.data.entities.Song

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE id = :id") //송 id를 받으면 그에 맞는 음악으로~
    fun getSong(id: Int): Song?

    @Query("UPDATE SongTable SET isLike= :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean, id: Int) //송 테이블에 매개변수를 던져준 아이디에 라이크 값을 업데이틀 해주려고 함.

    @Query("SELECT * FROM SongTable WHERE isLike= :isLike") //송 id를 받으면 그에 맞는 음악으로~
    fun getlikedSong(isLike: Boolean): List<Song>

    @Query("UPDATE SongTable SET isLike = 0 WHERE isLike = 1")
    fun updateAllIsLikeFalse()


}