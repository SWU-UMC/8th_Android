package com.example.workbook3

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.workbook3.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var currentPage = 0
    private val slideHandler = Handler(Looper.getMainLooper())
    private lateinit var slideRunnable: Runnable
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = HomeBackgroundVpAdapter(this)
        binding.homePannelBackgroundVp.adapter = adapter
        slideRunnable = object : Runnable {
            override fun run() {
                val pageCount = adapter.itemCount
                currentPage = (currentPage + 1) % pageCount
                binding.homePannelBackgroundVp.setCurrentItem(currentPage, true)
                slideHandler.postDelayed(this, 2000) // 2초마다 실행
            }
        }
        slideHandler.postDelayed(slideRunnable, 2000)

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val backgroundImages = listOf(
            R.drawable.img_first_album_default,
            R.drawable.img_home_viewpager_exp,
            R.drawable.img_home_viewpager_exp2
        )

        val backgroundAdapter = HomeBackgroundVpAdapter(backgroundImages)
        binding.homePannelBackgroundVp.adapter = backgroundAdapter
        binding.homeBackgroundIndicator.setViewPager(binding.homePannelBackgroundVp)
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homeAlbumImgIv1.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, AlbumFragment()).commitAllowingStateLoss()
        }

        val bannerAdapter = BannerVpAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        slideHandler.postDelayed(slideRunnable, 2000)
    }
}