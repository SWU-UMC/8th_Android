package com.example.mission_3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mission_3.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    lateinit var binding: FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container, false)
        binding.songMixoffTg.setOnClickListener {
            binding.songMixoffTg.visibility = View.GONE
            binding.songMixonTg.visibility = View.VISIBLE
        }

        binding.songMixonTg.setOnClickListener {
            binding.songMixoffTg.visibility = View.VISIBLE
            binding.songMixonTg.visibility = View.GONE
        }

        binding.songLalacLayout.setOnClickListener {
            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
        }

        binding.songFluLayout.setOnClickListener {
            Toast.makeText(activity,"FLU", Toast.LENGTH_SHORT).show()
        }

        binding.songCoinLayout.setOnClickListener {
            Toast.makeText(activity,"Coin", Toast.LENGTH_SHORT).show()
        }

        binding.songSpringLayout.setOnClickListener {
            Toast.makeText(activity,"봄 안녕 봄", Toast.LENGTH_SHORT).show()
        }

        binding.songCelebrityLayout.setOnClickListener {
            Toast.makeText(activity,"Celebrity", Toast.LENGTH_SHORT).show()
        }

        binding.songSingLayout.setOnClickListener {
            Toast.makeText(activity,"돌림노래 (Feat. DEAN)", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}