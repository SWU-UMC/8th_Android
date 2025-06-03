package com.example.a3week.ui.signin

import com.example.a3week.data.remote.Result

interface LoginView {
    fun onLoginSuccess(code : Int, result : Result)
    fun onLoginFailure()
}