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
//        binding.homeAlbumImgIv1.setOnClickListener {
//            val albumFragment = AlbumFragment().apply {
//                arguments = Bundle().apply {
//                    putString("albumTitle", "앨범 제목 예시")  // 원하는 제목 입력
//                    putInt("albumImageResId", imgRes)       // 현재 이미지 전달
//                }
//            }
//
//
//
//        }
        //-> 프래그먼트로 바꾸었음.
//        binding.homeAlbumImgIv1.setOnClickListener {
//            val activity = requireActivity() as? MainActivity
//            activity?.openAlbumFragment(
//                albumTitle = "LILAC",  // 앨범 제목 전달
//                albumImageResId = imgRes  // 현재 이미지 리소스 전달
//            )
//        }
        // 이미지 클릭 시 앨범 프래그먼트로 이동 + 동적으로 텍스트 전달
        binding.homeAlbumImgIv1.setOnClickListener {
            val activity = requireActivity() as? MainActivity

            // TextView에서 앨범 제목을 가져와 전달
            val albumTitle = binding.panelAlbumNameTv1.text.toString()

            activity?.openAlbumFragment(
                albumTitle = albumTitle,
                albumImageResId = imgRes
            )
        }



            //binding.hometodaymusicoverseahs.setImageResource(imgRes)
            return binding.root
        }
    }
