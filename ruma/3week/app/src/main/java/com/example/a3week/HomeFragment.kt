package com.example.a3week

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.a3week.databinding.FragmentHomeBinding
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    private lateinit var autoSlideExecutor: ScheduledExecutorService
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeAlbumImgVp1.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlbumFragment()).commitAllowingStateLoss()
        }

        // 배너 ViewPager 설정
        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homeBannerIndicator.setViewPager(binding.homeBannerVp)

        // 자동 슬라이드 시작
        startAutoSlide(bannerAdapter)

        // 패널 배경 ViewPager 설정
        val pannelAdpater = PannelVpAdapter(this)
        pannelAdpater.addFragment(PannelFragment(R.drawable.img_first_album_default))
        pannelAdpater.addFragment(PannelFragment(R.drawable.img_first_album_default))
        binding.homePannelBackgroundVp.adapter = pannelAdpater
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)

        return binding.root
    }

    private fun startAutoSlide(adapter: BannerVPAdapter) {
        autoSlideExecutor = Executors.newSingleThreadScheduledExecutor()
        autoSlideExecutor.scheduleAtFixedRate({
            handler.post {
                val nextItem = binding.homeBannerVp.currentItem + 1
                if (nextItem < adapter.itemCount) {
                    binding.homeBannerVp.currentItem = nextItem
                } else {
                    binding.homeBannerVp.currentItem = 0 // 순환
                }
            }
        }, 3000, 3000, TimeUnit.MILLISECONDS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoSlideExecutor.shutdown()
    }
}