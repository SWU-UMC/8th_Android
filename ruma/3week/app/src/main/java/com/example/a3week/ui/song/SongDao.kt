package com.example.a3week.ui.song

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.a3week.data.entities.Song

@Dao
interface SongDao {
    //기본 CRUD
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSong(id: Int): Song
    //좋아요 관련
    @Query("SELECT * FROM SongTable WHERE isLike = :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>

    @Query("UPDATE SongTable SET isLike = :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean, id: Int)

    @Query("UPDATE SongTable SET isLike = 0 WHERE isLike = 1")
    fun unlikeAll()
    //삭제 관련
    @Query("DELETE FROM SongTable WHERE isLike = 0")
    fun deleteUnlikedSongs()

    @Query("DELETE FROM SongTable WHERE id = :id")
    fun deleteById(id: Int)

}