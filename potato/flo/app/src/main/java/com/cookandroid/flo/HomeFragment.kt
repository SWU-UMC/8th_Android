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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import me.relex.circleindicator.CircleIndicator3
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


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


        try {
            // 👉 SharedPreferences 초기화 플래그 강제로 false 설정
            val prefs = requireContext().getSharedPreferences("album_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("isAlbumInserted", false).apply()

            // 👉 더미 앨범 삽입
            inputDummyAlbumsOnce()

        } catch (e: Exception) {
            Log.e("HomeFragment", "inputDummyAlbumsOnce() 오류: ${e.message}")
        }

        initAlbumRecyclerView() // ✅ 앨범 RecyclerView 초기화

        // 👇 아래는 그대로 두셔도 됩니다
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


    private fun inputDummyAlbumsOnce() {
        val context = binding.root.context // ✅ 안전한 context
        val prefs = context.getSharedPreferences("album_prefs", Context.MODE_PRIVATE)

        if (prefs.getBoolean("isAlbumInserted", false)) return

        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser"
        val albumRef = database.getReference("users/$userId/albums")

        albumRef.removeValue()

        val albumList = listOf(
            Album(1, "LILAC", "아이유 (IU)", R.drawable.img_album_exp2, "music_lilac"),
            Album(2, "See Me gwisun", "Daeseong", R.drawable.see_me, "music_seeme"),
            Album(3, "Sign", "Izna", R.drawable.izna_sign, "music_sign"),
            Album(4, "Like Jennie", "Jennie", R.drawable.jennie_like_jennie, "music_likejennie"),
            Album(5, "Whiplash", "aespa (에스파)", R.drawable.aespa_whiplash, "music_whiplash"),
            Album(6, "Extral", "Jennie", R.drawable.jennie_extral, "music_extral")
        )

        albumList.forEach { album ->
            albumRef.child(album.id.toString()).setValue(album)
        }

        prefs.edit().putBoolean("isAlbumInserted", true).apply()
    }

    // DB에서 Album을 가져와 RecyclerView에 표시
    private fun initAlbumRecyclerView() {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser"
        val albumRef = database.getReference("users/$userId/albums")

        albumRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val albumList = mutableListOf<Album>()
                for (albumSnapshot in snapshot.children) {
                    val album = albumSnapshot.getValue(Album::class.java)
                    if (album != null) {
                        albumList.add(album)
                    }
                }

                albumDatas = ArrayList(albumList)
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
                        val song = SaveSong( // Song 대신 SaveSong 사용
                            title = album.title ?: "",
                            singer = album.singer ?: "",
                            coverImg = album.coverImg ?: 0,
                            isChecked = false,
                            isLike = false,
                            id = album.id,
                            music = album.music,
                            playtime = 60,
                            isPlaying = true,
                            second = 0
                        )

                        val sharedPreferences = requireActivity()
                            .getSharedPreferences("song", AppCompatActivity.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        val songJson = Gson().toJson(song)
                        editor.putString("songData", songJson)
                        editor.apply()

                        (activity as? MainActivity)?.setMiniPlayer(song)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read albums: ${error.message}")
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



