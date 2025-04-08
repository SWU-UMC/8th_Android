package com.example.a3week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.a3week.databinding.FragmentDetailBinding
import com.example.a3week.databinding.FragmentSongBinding
import java.util.zip.Inflater

class SongFragment : Fragment() {
    lateinit var binding: FragmentSongBinding
    var isMixOn = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        // TextView 눌렀을 때 toggle 이미지 바꾸기
        binding.songMixTv.setOnClickListener {
            isMixOn = !isMixOn
            if (isMixOn) {
                binding.songMixoffTg.visibility = View.GONE
                binding.songMixonTg.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "취향대로 MIX했어요!", Toast.LENGTH_SHORT).show()
            } else {
                binding.songMixoffTg.visibility = View.VISIBLE
                binding.songMixonTg.visibility = View.GONE
                Toast.makeText(requireContext(), "원래대로 돌렸어요!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
