package com.example.a3week.ui.song

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.a3week.data.local.UserDao
import com.example.a3week.data.entities.Album
import com.example.a3week.data.entities.Like
import com.example.a3week.data.entities.Song
import com.example.a3week.data.entities.User
import com.example.a3week.ui.main.album.AlbumDao

@Database(entities = [Song::class, Album::class, User::class, Like::class], version = 4,exportSchema = false )
abstract class SongDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao
    abstract fun userDao() : UserDao

    companion object {
        @Volatile
        private var INSTANCE: SongDatabase? = null

        fun getInstance(context: Context): SongDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SongDatabase::class.java,
                    "song-database"
                )
                    .fallbackToDestructiveMigration() // 🚨 이 줄 추가!
                    .allowMainThreadQueries() // ⚠️ 개발 중 임시 허용
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}