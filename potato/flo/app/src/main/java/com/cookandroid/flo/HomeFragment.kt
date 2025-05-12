package com.cookandroid.flo

import android.content.Context
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
    private lateinit var albumRVAdapter: AlbumRVAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val slideRunnable = Runnable { slideToNextPage() }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        inputDummyAlbumsOnce()     // ✅ 중복 방지하여 더미 앨범 삽입
        initAlbumRecyclerView()    // ✅ 앨범 RecyclerView 초기화

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val panelTopAdapter = TopPanelVpAdapter(this)
        panelTopAdapter.addFragment(HomeTopPanelFragment(R.drawable.img_first_album_default))
        panelTopAdapter.addFragment(HomeTopPanelFragment(R.drawable.img_first_album_default))
        panelTopAdapter.addFragment(HomeTopPanelFragment(R.drawable.img_first_album_default))
        binding.homeTopPannelVp.adapter = panelTopAdapter
        binding.homeTopPannelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator: CircleIndicator3 = binding.homePannelIndicator
        indicator.setViewPager(binding.homeTopPannelVp)

        startAutoSlide()

        return binding.root
    }

    // ✅ Room에 더미 Album 2개 삽입 (중복 삽입 방지)
    private fun inputDummyAlbumsOnce() {
        val prefs = requireContext().getSharedPreferences("album_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("isAlbumInserted", false).apply()
        // ✅ 기존 데이터를 전부 삭제 (테스트용 초기화 목적)
        val albumDB = SongDatabase.getInstance(requireContext())
        albumDB.albumDao().deleteAll()

        //prefs.edit().clear().apply()
        //prefs.edit().remove("isSongInserted").apply() // 더미 데이터 다시 추가
        //prefs.edit().putBoolean("isAlbumInserted", false).apply()
        if (prefs.getBoolean("isAlbumInserted", false)) return

        //val albumDB = SongDatabase.getInstance(requireContext())

        albumDB.albumDao().insert(
            Album(
                id = 1,
                title = "LILAC",
                singer = "아이유 (IU)",
                coverImg = R.drawable.img_album_exp2,
                music = "music_lilac"
            )
        )

        albumDB.albumDao().insert(
            Album(
                id = 2,
                title = "See Me gwisun",
                singer = "Daeseong",
                coverImg = R.drawable.see_me,
                music = "music_seeme"
            )
        )

        albumDB.albumDao().insert(
            Album(
                id = 3,
                title = "Sign",
                singer = "Izna",
                coverImg = R.drawable.izna_sign,
                music = "music_sign"
            )
        )

        albumDB.albumDao().insert(
            Album(
                id = 4,
                title = "Like Jennie",
                singer = "Jennie",
                coverImg = R.drawable.jennie_like_jennie,
                music = "music_likejennie"
            )
        )

        albumDB.albumDao().insert(
            Album(
                id = 5,
                title = "Whiplash",
                singer = "aespa (에스파)",
                coverImg = R.drawable.aespa_whiplash,
                music = "music_whiplash"
            )
        )

        albumDB.albumDao().insert(
            Album(
                id = 6,
                title = "Extral",
                singer = "Jennie",
                coverImg = R.drawable.jennie_extral,
                music = "music_extral"
            )
        )

        prefs.edit().putBoolean("isAlbumInserted", true).apply()
    }

    // ✅ DB에서 Album을 가져와 RecyclerView에 표시
    private fun initAlbumRecyclerView() {
        val albumDB = SongDatabase.getInstance(requireContext())
        albumDatas = ArrayList(albumDB.albumDao().getAlbums())

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayAlbumRv.adapter = albumRVAdapter
        binding.homeTodayAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
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
                    music = album.music,
                    albumIdx = album.id //추가

                )
                val sharedPreferences =
                    requireActivity().getSharedPreferences("song", AppCompatActivity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val songJson = Gson().toJson(song)
                editor.putString("songData", songJson)
                editor.apply()

                (activity as? MainActivity)?.setMiniPlayer(song)
            }
        })
    }

    // ✅ 앨범 클릭 시 AlbumFragment로 이동
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

    // 🔁 자동 슬라이드 기능
    private fun slideToNextPage() {
        val viewPager = binding.homeTopPannelVp
        val nextItem = (viewPager.currentItem + 1) % viewPager.adapter!!.itemCount
        viewPager.setCurrentItem(nextItem, true)
        handler.postDelayed(slideRunnable, 5000)
    }

    private fun startAutoSlide() {
        handler.postDelayed(slideRunnable, 5000)
    }

    private fun stopAutoSlide() {
        handler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        startAutoSlide()
         // 예시: 이 함수에서 DB를 다시 읽어야 함
        // ✅ 어댑터 갱신만으로 해결
        //albumRVAdapter.notifyDataSetChanged()
        if (::albumRVAdapter.isInitialized) {
            albumRVAdapter.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoSlide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(slideRunnable)
    }
}



