package com.cookandroid.flo.Api

import com.cookandroid.flo.data.remote.Result

interface LoginView {
    fun onLoginSuccess(code : Int, result : Result)
    fun onLoginFailure()
}