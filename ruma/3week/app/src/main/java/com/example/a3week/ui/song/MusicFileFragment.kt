package com.example.a3week.ui.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a3week.databinding.FragmentMusicFileBinding

class MusicFileFragment: Fragment() {

    lateinit var binding: FragmentMusicFileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMusicFileBinding.inflate(inflater, container, false)

        return binding.root
    }
}