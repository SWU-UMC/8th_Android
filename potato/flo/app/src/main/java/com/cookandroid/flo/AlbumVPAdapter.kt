package com.cookandroid.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
class AlbumVPAdapter(fragment:Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongListFragment()    // 수록곡 탭
            1 -> DetailFragment()      // 상세정보 탭
            else -> VideoFragment()    // 영상 탭
        }
    }

}
