package com.cookandroid.flo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.flo.databinding.ActivitySignupBinding


class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpSignUpBtn.setOnClickListener {
            signUp()
            finish()
        }
    }

    private fun getUser(): User { //사용자가 입력한 값을 가져옴
        val email: String = binding.signUpIdEt.text.toString() + "@" + binding.signUpDirectInputEt.text.toString()
        val pwd: String = binding.signUpPasswordEt.text.toString() //스트링으로 변환이 필요함.

        return User(email, pwd) //사용자의 값을 리턴함.
        //editText를 이용해 사용자가 입력한 값을 가져옴
    }

    private fun signUp() { //회원가입 진행 함수
        if (binding.signUpIdEt.text.toString().isEmpty() || binding.signUpDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
            //사실을 더 정확히 조건을 걸어야 하나 우리는 연습이기에 여기까지.
        }

        if (binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        //아래의 작업을 모두 완료하면, 이렇게 잘 작동을 하게 됨.

        val userDB = SongDatabase.getInstance(this)!! //유저db에 추가
        userDB.userDao().insert(getUser())

        val users = userDB.userDao().getUsers()

        Log.d("SIGNUPACT", users.toString())
    }
}