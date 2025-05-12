package com.cookandroid.flo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlbumDao {

    @Insert
    fun insert(album: Album)

    @Insert
    fun insertAll(albums: List<Album>)

    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE id = :albumId")
    fun getAlbumById(albumId: Int): Album

    @Query("DELETE FROM AlbumTable")
    fun deleteAll()
}
