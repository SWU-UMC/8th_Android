package com.example.a3week

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class, Album::class], version = 1)
abstract class SongDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: SongDatabase? = null

        fun getInstance(context: Context): SongDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SongDatabase::class.java,
                    "song-database"
                ).allowMainThreadQueries() // 임시로 main thread 허용 (비추천, 실무에서는 coroutine이나 LiveData 사용)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}