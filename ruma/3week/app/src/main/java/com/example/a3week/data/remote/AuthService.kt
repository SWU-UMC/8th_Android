package com.example.a3week.data.remote

import android.util.Log
import com.example.a3week.ui.signin.LoginRequest
import com.example.a3week.ui.signin.LoginView
import com.example.a3week.ui.signup.SignUpView
import com.example.a3week.data.entities.User
import com.example.a3week.utils.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView

    fun setSignUpView(signUpView: SignUpView) {
        this.signUpView = signUpView
    }

    fun setLoginView(loginView: LoginView) {
        this.loginView = loginView
    }

    fun signUp(user: User) {

        val signUpService = getRetrofit().create(AuthRetrofitInterface::class.java)

        signUpService.signUp(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val signUpResponse: AuthResponse = response.body()!!

                    Log.d("SIGNUP-RESPONSE", signUpResponse.toString())
                    val resp: AuthResponse=response.body()!!
                    when (resp.code) {
                        1000 -> signUpView.onSignUpSuccess()
                        else-> {
                            signUpView.onSignUpFailure()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                //실패처리
            }
        })
        Log.d("LOGIN","HELLO")
    }


    fun login(email: String, password: String) {
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)
        val loginRequest = LoginRequest(email, password)

        authService.login(loginRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("LOGIN/SUCCESS", "code: ${response.code()}, body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!

                    when (resp.code) {
                        1000 -> loginView.onLoginSuccess(resp.code, resp.result!!)
                        else -> loginView.onLoginFailure()
                    }
                } else {
                    Log.e("LOGIN/FAIL", "응답 실패. 코드: ${response.code()}, 에러 바디: ${response.errorBody()?.string()}")
                    loginView.onLoginFailure()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e("LOGIN/FAILURE", "네트워크 실패: ${t.message}")
                loginView.onLoginFailure()
            }
        })
    }
}