package com.cookandroid.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cookandroid.flo.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import androidx.appcompat.app.AppCompatActivity
import me.relex.circleindicator.CircleIndicator3


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()

    private val handler = Handler(Looper.getMainLooper()) // 🔹 핸들러 추가

    private val slideRunnable = Runnable { slideToNextPage() } // 🔹 Runnable 추가

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


//        binding.homeAlbumImgIv1.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm,AlbumFragment())
//                .commitAllowingStateLoss()
//        }

        albumDatas.apply {
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2,music = "music_lilac")) //음악 재생을 위함.
            add(Album("Butter", "방탄소년단(BTS)", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
        }

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        //리사이클 뷰에 어뎁터 연결!
        binding.homeTodayAlbumRv.adapter = albumRVAdapter
        binding.homeTodayAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{


            override fun onItemClick(album: Album) {
                extracted(album)

            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            override fun onPlayClick(album: Album) {

                Log.d("HomeFragment", "Play button clicked: ${album.music}")
                val song = Song(
                    title = album.title ?: "",
                    singer = album.singer ?: "",
                    second = 0,
                    playtime = 60,
                    isPlaying = true,
                    music = album.music)
                // SharedPreferences에 저장
                val sharedPreferences = requireActivity().getSharedPreferences("song", AppCompatActivity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val songJson = Gson().toJson(song)
                editor.putString("songData", songJson)
                editor.apply()

                // 수정: MainActivity에 전달 (null 체크 + 음악 존재 여부는 MainActivity에서 처리)
                (activity as? MainActivity)?.setMiniPlayer(song)

            }
        })
        //리사이클뷰에 아이템을 클릭했을 때, 프래그먼트로 전환.

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

//        val panelAdapter = PanelVPAdapter(this)
//        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp2))
//        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp2))
//        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp2))
//
//        binding.homePanelVp.adapter = panelAdapter
//        binding.homePanelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        val panelTopAdapter = TopPanelVpAdapter(this)
        panelTopAdapter.addFragment(HomeTopPanelFragment(R.drawable.img_first_album_default))
        panelTopAdapter.addFragment(HomeTopPanelFragment(R.drawable.img_first_album_default))
        panelTopAdapter.addFragment(HomeTopPanelFragment(R.drawable.img_first_album_default))
        binding.homeTopPannelVp.adapter = panelTopAdapter
        binding.homeTopPannelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator: CircleIndicator3 = binding.homePannelIndicator  //home_pannel_indicator
        indicator.setViewPager(binding.homeTopPannelVp)

        // 어댑터 변경 감지
        //panelTopAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)

        //panelTopAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver()); => 적용 불가!

        // 자동 슬라이드 시작
        startAutoSlide()

        return binding.root
    }

    // 수정: 앨범 클릭 시 상세 페이지 이동 (원래 코드 유지)
    private fun extracted(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }


    //여기는 잘 몰라서, 찾아봄.
   private fun slideToNextPage() {
        val viewPager = binding.homeTopPannelVp
        val nextItem = (viewPager.currentItem + 1) % viewPager.adapter!!.itemCount
        viewPager.setCurrentItem(nextItem, true)
        handler.postDelayed(slideRunnable, 5000) // 5초 후 다시 실행
    }

    private fun startAutoSlide() {
        handler.postDelayed(slideRunnable, 5000)
    }

    private fun stopAutoSlide() {
        handler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        startAutoSlide() // 피드백을 바탕으로 추가 수정.
    }

    override fun onPause() {
        super.onPause()
        stopAutoSlide() //피드백을 토대로 수정!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(slideRunnable) // 메모리 누수 방지
    }
}



