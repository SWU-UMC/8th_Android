package com.example.a3week.ui.main.album

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.a3week.data.entities.Album
import com.example.a3week.data.entities.Like

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)

    @Insert
    fun likeAlbum(like : Like)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable") // 테이블의 모든 값을 가져오기
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album

    @Query("select id from LikeTable where userId =:userId and albumId = :albumId")
    fun isLikedAlbum(userId : Int, albumId : Int) : Int?

    @Query("delete from LikeTable where userId =:userId and albumId = :albumId")
    fun dislikedAlbum(userId : Int, albumId : Int)

    @Query("select at.* from LikeTable as lt left join AlbumTable as at on lt.albumId = at.id where lt.userId = :userId")
    fun getLikedAlbums(userId : Int) : List<Album>
}