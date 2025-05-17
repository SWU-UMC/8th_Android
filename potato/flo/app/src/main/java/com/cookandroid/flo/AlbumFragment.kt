package com.cookandroid.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cookandroid.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var isLiked : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        //패널 프래그먼트에서 전달 받은 데이터 가져오기.
        val albumTitle = arguments?.getString("albumTitle") ?: "앨범 제목 없음"
        val singerName = arguments?.getString("singerName") ?: "가수 이름 없음"
        val albumImageResId = arguments?.getInt("albumImageResId") ?: R.drawable.img_album_exp2

        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson,Album::class.java)
        isLiked = isLikedAlbum(album.id)
        setInit(album)
        setClickListeners(album) //현재 저장된 앨범의 데이터


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

    private fun setInit(album: Album){
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()

// 좋아요 아이콘은 별도의 ImageView에 적용
        if(isLiked){
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)  // ✅ 요기로 옮김
        } else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val jwt = spf!!.getInt("jwt", 0)
        Log.d("MAIN_ACT/GET_JWT", "jwt_token: $jwt")

        return jwt
    }

    private fun likeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId) //앨범을 좋아요 눌렀을 때 좋아요 표시를 하기 위함.

        songDB.albumDao().likeAlbum(like)
    }

    private fun isLikedAlbum(albumId: Int): Boolean { //홈 화면에서 사용자가 좋아요를 눌렀는지 아닌지 확인을 하기 위함.
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId: Int? = songDB.albumDao().isLikedAlbum(userId, albumId)

        return likeId != null
    }

    private fun disLikeAlbum(userId: Int, albumId: Int) { //사용자가 좋아요를 누른 것을 삭제하면, 하는 후속 조치.
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()
        songDB.albumDao().disLikeAlbum(userId, albumId)
    }

    private fun setClickListeners(album: Album) {
        val userId: Int = getJwt()

        binding.albumLikeIv.setOnClickListener {
            if(isLiked) {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikeAlbum(userId, album.id)
            } else {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }

            isLiked = !isLiked
        }

        //set click listener
        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }
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

