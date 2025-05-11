package com.cookandroid.flo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version =1 )
abstract class SongDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao //룸은 데이터베이스에 지정된 객체와 연결된 dao를 반드시 명시!

    companion object{
        private var instance : SongDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : SongDatabase {
            if(instance == null)
            {
                synchronized(SongDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database" //다른 데이터 베이스랑 이름 겹치면 꼬임

                    ).allowMainThreadQueries().build() //쓰레드랑 데이터베이스 연동
                }
            }

            return instance!! //non-null을 반환하므로 !! 사용 오류를 잡기 위하.
        }
    }
}

