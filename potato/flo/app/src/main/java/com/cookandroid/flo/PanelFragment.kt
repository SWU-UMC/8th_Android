package com.cookandroid.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.cookandroid.flo.databinding.FragmentPanelBinding

class PanelFragment(val imgRes : Int) : Fragment() {

    lateinit var binding : FragmentPanelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPanelBinding.inflate(inflater,container,false)

        binding.homeAlbumImgIv1.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm,AlbumFragment())
                .commitAllowingStateLoss()
        }

        //binding.hometodaymusicoverseahs.setImageResource(imgRes)
        return binding.root
    }
}