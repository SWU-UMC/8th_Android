package com.example.a3week

import SavedSongRVAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a3week.databinding.FragmentLockerSavedsongBinding


class SavedSongFragment : Fragment() {
    lateinit var binding: FragmentLockerSavedsongBinding
    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!
        initRecyclerview()

        return binding.root
    }
    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val likedSongs = songDB.songDao().getLikedSongs(true)
        (binding.lockerSavedSongRecyclerView.adapter as? SavedSongRVAdapter)?.updateSongs(likedSongs)
    }

    fun refreshSongList() {
        val likedSongs = songDB.songDao().getLikedSongs(true)
        (binding.lockerSavedSongRecyclerView.adapter as? SavedSongRVAdapter)?.let {
            it.updateSongs(likedSongs)
        }
    }

    private fun initRecyclerview(){

        binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdapter = SavedSongRVAdapter()

        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
                refreshSongList()
            }
        })

        binding.lockerSavedSongRecyclerView.adapter = songRVAdapter

        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
    }
    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf?.getInt("jwt", 0) ?: 0
    }
}