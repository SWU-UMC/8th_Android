package com.example.a3week.data.remote

import com.example.a3week.ui.signin.LoginRequest
import com.example.a3week.data.entities.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRetrofitInterface {
    @POST("/users")
    fun signUp(@Body user: User): Call<AuthResponse>

    @POST("/users/login")
    fun login(@Body loginRequest: LoginRequest): Call<AuthResponse>
}