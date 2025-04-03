package com.cookandroid.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cookandroid.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        //패널 프래그먼트에서 전달 받은 데이터 가져오기.
        val albumTitle = arguments?.getString("albumTitle") ?: "앨범 제목 없음"
        val albumImageResId = arguments?.getInt("albumImageResId") ?: R.drawable.img_album_exp2

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumCententTb, binding.albumContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }
}

        /*과제 이어, 혹시 스위치 버튼 선택시... 바뀜!
        binding.albumSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {

                binding.albumMusicTitleTv.text = "BTS New Album 'Dynamite'"
                binding.albumSingerNameTv.text = "BTS"
                binding.albumMusicTitleInfoTv.text = "2025.04.01 | 싱글 | 팝"
                binding.albumAlbumIv.setImageResource(R.drawable.img_album_exp)
                binding.songMusicTitle01Tv.text="Butter"
                binding.songSingerName01Tv.text="BTS"

            } else {
                // 기존 앨범 정보로 변경
                binding.albumMusicTitleTv.text = "U 5th Album 'LILAC'"
                binding.albumSingerNameTv.text = "IU"
                binding.albumMusicTitleInfoTv.text = "2021.05.21 | 정규 | 댄스 팝"
                binding.albumAlbumIv.setImageResource(R.drawable.img_album_exp2)

            }
        }*/


        /*binding.songLalacLayout.setOnClickListener {
            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
        }

        binding.songFluLayout.setOnClickListener {
            Toast.makeText(activity,"FLU", Toast.LENGTH_SHORT).show()
        }

        binding.songCoinLayout.setOnClickListener {
            Toast.makeText(activity,"Coin", Toast.LENGTH_SHORT).show()
        }

        binding.songSpringLayout.setOnClickListener {
            Toast.makeText(activity,"봄 안녕 봄", Toast.LENGTH_SHORT).show()
        }

        binding.songCelebrityLayout.setOnClickListener {
            Toast.makeText(activity,"Celebrity", Toast.LENGTH_SHORT).show()
        }

        binding.songSingLayout.setOnClickListener {
            Toast.makeText(activity,"돌림노래 (Feat. DEAN)", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }*/

