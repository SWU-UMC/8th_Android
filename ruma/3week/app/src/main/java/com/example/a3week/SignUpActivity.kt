package com.example.a3week

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a3week.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), SignUpView {

    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpSignUpBtn.setOnClickListener {
            signUp()
        }
    }

    private fun getUser(): User {
        val email = binding.signUpIdEt.text.toString() + "@" + binding.signUpDirectInputEt.text.toString()
        val password = binding.signUpPasswordEt.text.toString()
        val name = binding.signUpNameEt.text.toString()

        return User(email, password, name)
    }

    private fun signUp() {
        if (binding.signUpIdEt.text.toString().isEmpty() ||
            binding.signUpDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.signUpNameEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이름 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

//        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)
//
//        authService.signUp(getUser()).enqueue(object : Callback<AuthResponse> {
//            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
//                Log.d("SIGNUP/SUCCESS", response.toString())
//
//                response.body()?.let { resp ->
//                    when (resp.code) {
//                        1000 -> {
//                            Toast.makeText(this@SignUpActivity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
//                            finish()
//                        }
//                        2016, 2017 -> {
//                            binding.signUpEmailErrorTv.text = resp.message
//                            binding.signUpEmailErrorTv.visibility = View.VISIBLE
//                        }
//                        2018 -> {
//                            // 이메일 형식 오류 같은 기타 응답 처리
//                            Toast.makeText(this@SignUpActivity, resp.message, Toast.LENGTH_SHORT).show()
//                        }
//                        else -> {
//                            Toast.makeText(this@SignUpActivity, "알 수 없는 오류: ${resp.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } ?: run {
//                    Log.e("SIGNUP/RESPONSE", "응답 본문이 null입니다.")
//                }
//            }
//
//            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                Log.e("SIGNUP/ERROR", "네트워크 실패: ${t.message}", t)
//                Toast.makeText(this@SignUpActivity, "서버 연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        })
        val authService= AuthService()
        authService.setSignUpView(this)

        authService.signUp(getUser())
    }

    override fun onSignUpSuccess() {
        finish()
    }

    override fun onSignUpFailure() {
        TODO("Not yet implemented")
    }
}
