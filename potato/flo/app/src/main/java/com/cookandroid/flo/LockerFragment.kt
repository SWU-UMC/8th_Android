package com.cookandroid.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cookandroid.flo.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator


class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding
    private val information = arrayListOf("저장한 곡", "음악파일","저장 앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerViewpager.adapter = lockerAdapter
        TabLayoutMediator(binding.lockerTablayout,binding.lockerViewpager){
                tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener{
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initViews()
    }


    //jwt를 저장했던 auth를 가져오고, 액티비티에 물음표를 적는 이유는 프래그먼트에서 사용하는 방법이기 때문!
    private fun getJwt():Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt",0) //sharedPrefersence에서 가져온 값이 없다면, 0 을 반환하기 위함.

    }

    //뒤의 text를 로그인 or 로그아웃을 할지 결정을 함.
    private fun initViews(){
        val jwt : Int = getJwt()
        if(jwt == 0){
            //로그인을 해야하는 상황
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener{
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        else{
            binding.lockerLoginTv.text = "로그아웃"

            binding.lockerLoginTv.setOnClickListener {
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logout() {
        //SharedPreferences에 값을 없애주는 역할을 함.
        val spf = activity?.getSharedPreferences("auth" , AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("jwt")
        editor.apply()
    }
}