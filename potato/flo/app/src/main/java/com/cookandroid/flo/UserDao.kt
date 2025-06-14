package com.cookandroid.flo


import androidx.room.*
import com.cookandroid.flo.data.entities.User


@Dao
interface UserDao {
    @Insert
    fun insert(user: User) //

    @Query("SELECT * FROM UserTable")
    fun getUsers(): List<User>

    @Query("SELECT * FROM UserTable WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?
}