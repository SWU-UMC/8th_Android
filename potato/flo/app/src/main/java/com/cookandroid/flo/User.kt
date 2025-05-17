package com.cookandroid.flo

import androidx.room.Entity
import androidx.room.PrimaryKey

//테이블이기에 엔티티를 붙임!
//사용자 이메일, 패스워드를 넣어주어야 함.

@Entity(tableName = "UserTable")
data class User(
    var email: String,
    var password: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}