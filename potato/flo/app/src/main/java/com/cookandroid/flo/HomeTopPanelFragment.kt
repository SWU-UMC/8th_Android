package com.cookandroid.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.cookandroid.flo.databinding.FragmentHomeTopPanelBinding

class HomeTopPanelFragment(val imgRes : Int) : Fragment() {

    lateinit var binding : FragmentHomeTopPanelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeTopPanelBinding.inflate(inflater,container,false)



        //binding.hometodaymusicoverseahs.setImageResource(imgRes)
        return binding.root
    }
}