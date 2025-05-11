package com.cookandroid.flo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)
}