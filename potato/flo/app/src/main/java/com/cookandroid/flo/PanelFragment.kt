package com.cookandroid.flo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.cookandroid.flo.databinding.FragmentPanelBinding

class PanelFragment(val imgRes : Int) : Fragment() {

    lateinit var binding: FragmentPanelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPanelBinding.inflate(inflater, container, false)

        //이미지 클릭 시 앨범프래그먼트로 이동 + 데이터전달.
        binding.homeAlbumImgIv1.setOnClickListener {
            val albumFragment = AlbumFragment().apply {
                arguments = Bundle().apply {
                    putString("albumTitle", "앨범 제목 예시")  // 원하는 제목 입력
                    putInt("albumImageResId", imgRes)       // 현재 이미지 전달
                }
            }
        }

            //binding.hometodaymusicoverseahs.setImageResource(imgRes)
            return binding.root
        }
    }
