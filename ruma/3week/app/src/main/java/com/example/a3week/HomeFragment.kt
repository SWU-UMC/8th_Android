package com.example.a3week

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.a3week.databinding.FragmentHomeBinding
import com.google.gson.Gson
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment(), CommunicationInterface {

    lateinit var binding: FragmentHomeBinding

    private lateinit var autoSlideExecutor: ScheduledExecutorService
    private val handler = Handler(Looper.getMainLooper())
    private var albumDatas = ArrayList<Album>()
    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 1. Room DB 인스턴스 가져오기
        songDB = SongDatabase.getInstance(requireContext())!!

        // 2. 별도 쓰레드에서 더미 데이터 입력 후 UI 갱신
        Executors.newSingleThreadExecutor().execute {
            inputDummyAlbums()

            // DB에서 앨범 데이터 가져오기
            val albumsFromDB = songDB.albumDao().getAlbums()

            // 메인 스레드에서 UI 갱신
            handler.post {
                albumDatas.clear()
                albumDatas.addAll(albumsFromDB)
                Log.d("albumlist", albumDatas.toString())

                // RecyclerView 어댑터 및 레이아웃 매니저 설정
                val albumRVAdapter = AlbumRVAdapter(albumDatas)
                binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
                binding.homeTodayMusicAlbumRv.layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

                albumRVAdapter.setItemClickListener(object : AlbumRVAdapter.OnItemClickListener {
                    override fun onItemClick(album: Album) {
                        changeToAlbumFragment(album)
                    }

                    override fun onPlayAlbum(album: Album) {
                        sendData(album)
                    }
                })
            }
        }

        // 기존 배너 ViewPager 설정 등은 여기서 그대로 처리
        setupBannerViewPager()
        setupPanelViewPager()

        return binding.root
    }

    private fun inputDummyAlbums() {
        val songs = songDB.albumDao().getAlbums()
        if (songs.isNotEmpty()) return // 이미 데이터가 있으면 종료

        songDB.albumDao().insert(
            Album(1, "FRR", "George", R.drawable.img_album_exp)
        )
        songDB.albumDao().insert(
            Album(2, "IU 5th Album 'LILAC'","아이유 (IU)", R.drawable.img_album_exp2)
        )
        songDB.albumDao().insert(
            Album(3, "seasons flows 0.02", "wave to earth", R.drawable.img_album_exp3)
        )
        songDB.albumDao().insert(
            Album(4, "POP UP", "dragon pony", R.drawable.img_album_exp4)
        )
        songDB.albumDao().insert(
            Album(5, "The Volunteers", "the volunteers", R.drawable.img_album_exp5)
        )
        songDB.albumDao().insert(
            Album(6, "<January Never Dies>", "Balming Tiger", R.drawable.img_album_exp6)
        )
    }

    private fun setupBannerViewPager() {
        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homeBannerIndicator.setViewPager(binding.homeBannerVp)

        startAutoSlide(bannerAdapter)
    }

    private fun setupPanelViewPager() {
        val pannelAdapter = PannelVpAdapter(this)
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_first_album_default))
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_first_album_default))
        binding.homePannelBackgroundVp.adapter = pannelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)
    }

    private fun startAutoSlide(adapter: BannerVPAdapter) {
        autoSlideExecutor = Executors.newSingleThreadScheduledExecutor()
        autoSlideExecutor.scheduleAtFixedRate({
            handler.post {
                val nextItem = binding.homeBannerVp.currentItem + 1
                if (nextItem < adapter.itemCount) {
                    binding.homeBannerVp.currentItem = nextItem
                } else {
                    binding.homeBannerVp.currentItem = 0
                }
            }
        }, 3000, 3000, TimeUnit.MILLISECONDS)
    }

    private fun changeToAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumToJson = gson.toJson(album)
                    putString("album", albumToJson)
                }
            })
            .commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoSlideExecutor.shutdown()
    }

    override fun sendData(album: Album) {
        if (activity is MainActivity) {
            val activity = activity as MainActivity
            activity.updateMainPlayerCl(album)
        }
    }
}
