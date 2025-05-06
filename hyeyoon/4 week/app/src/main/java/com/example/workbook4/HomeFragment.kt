package com.example.workbook4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.workbook4.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private lateinit var viewpagerAdapter: PannelVPAdapter
    private var currentPage = 0
    private lateinit var pagerThread: Thread
    private var albumDatas = ArrayList<Album>()

    private val handler = Handler(Looper.getMainLooper()) {
        setPage()
        true
    }

    inner class PagerRunnable : Runnable {
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    Thread.sleep(2000)
                    handler.sendEmptyMessage(0)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
    }

    private fun setPage() {
        if (currentPage == 3) currentPage = 0
        binding.homePannelBackgroundVp.setCurrentItem(currentPage, true)
        currentPage += 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

//        binding.homeAlbumImgIv1.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, AlbumFragment()).commitAllowingStateLoss()
//        }

        // 데이터 리스트 생성 더미 데이터
        albumDatas.apply {
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp2))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp2))
        }

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val bannerAdapter = BannerVpAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewpagerAdapter = PannelVPAdapter(this)
        viewpagerAdapter.addFragment(PannelFragment())
        viewpagerAdapter.addFragment(PannelFragment())
        viewpagerAdapter.addFragment(PannelFragment())

        binding.homePannelBackgroundVp.adapter = viewpagerAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)

        pagerThread = Thread(PagerRunnable())
        pagerThread.start()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        pagerThread.interrupt()
    }
}