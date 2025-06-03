package com.cookandroid.flo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cookandroid.flo.data.entities.Like

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

    @Insert
    fun likeAlbum(like: Like) //like table, album 테이블 조인을 함. 사용자가 좋아하는 앨범..

    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun disLikeAlbum(userId: Int, albumId: Int)

    @Query("SELECT id FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun isLikedAlbum(userId: Int, albumId: Int): Int?

    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT ON LT.albumId = AT.id WHERE LT.userId = :userId")
    fun getLikedAlbums(userId: Int): List<Album> //좋아하는 앨범.. 가져오는...

    //조인을 한다 = 앨범 테이블을 라이크 테이블에 붙이는 것임. id가 같은 것끼리 붙여줌.
}
