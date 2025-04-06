package com.cookandroid.flo
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
//여러개의 프래그먼트를 담아줄 공간이 필요함.
    private val fragmentlist : ArrayList<Fragment> = ArrayList() //프라이베이트를 쓰면, 이 클래스에서만 사용이 가능함.(외부 접근 불가)


    override fun getItemCount(): Int {
        return fragmentlist.size  //리스프 안에 있는 개수를 가져오기 위해서.
    }

    override fun createFragment(position: Int): Fragment  = fragmentlist[position] //0,1,2,3

    fun addFragment(fragment: Fragment){
        fragmentlist.add(fragment)
        notifyItemInserted(fragmentlist.size-1) //뷰페이저에 새로운 값이 추가되어졌어. 새로운 곳이 리스트 추가된 곳을 알아내는 역할


    }
}