package com.example.a3week.ui.main.look

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a3week.ui.song.FloChartResult
import com.example.a3week.ui.song.SongRVAdapter
import com.example.a3week.ui.song.SongService
import com.example.a3week.databinding.FragmentLookBinding

class LookFragment : Fragment(), LookView {
    private lateinit var binding: FragmentLookBinding
    private lateinit var floCharAdapter: SongRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLookBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getSongs()
    }

    private fun initRecyclerView(result: FloChartResult) {
        floCharAdapter = SongRVAdapter(requireContext(), result)
        binding.lookFloChartRv.adapter = floCharAdapter
    }

    private fun getSongs() {
        val songService = SongService()
        songService.setLookView(this)

        songService.getSongs()

    }

    override fun onGetSongLoading() {
        binding.lookLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetSongSuccess(code: Int, result: FloChartResult) {
        Log.d("LOOK-FRAG", "곡 개수: ${result.songs.size}")
        result.songs.forEach {
            Log.d("LOOK-FRAG", "title: ${it.title}, singer: ${it.singer}")
        }

        binding.lookLoadingPb.visibility = View.GONE
        initRecyclerView(result)
    }

    override fun onGetSongFailure(code: Int, message: String) {
        binding.lookLoadingPb.visibility = View.GONE
        Log.d("LOOK-FRAG/SONG-RESPONSE", message)
    }
}