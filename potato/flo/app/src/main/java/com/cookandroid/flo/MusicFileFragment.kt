package com.cookandroid.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.flo.databinding.FragmentLockerMusicfileBinding
import com.cookandroid.flo.databinding.FragmentVideoBinding

class MusicFileFragment: Fragment() {

    lateinit var binding: FragmentLockerMusicfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLockerMusicfileBinding.inflate(inflater, container, false)

        return binding.root
    }
}