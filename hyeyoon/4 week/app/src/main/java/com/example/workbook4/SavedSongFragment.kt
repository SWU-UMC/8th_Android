package com.example.workbook4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workbook4.databinding.FragmentSavedSongBinding


class SavedSongFragment : Fragment() {
    lateinit var binding : FragmentSavedSongBinding
    private var savedsongDatas = ArrayList<SavedSong>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedSongBinding.inflate(inflater,container,false)

        savedsongDatas.apply {
            add(SavedSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(SavedSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(SavedSong("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp))
            add(SavedSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp2))
            add(SavedSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp))
            add(SavedSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp2))
            add(SavedSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(SavedSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(SavedSong("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp))
            add(SavedSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp2))
            add(SavedSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp))
            add(SavedSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp2))
            add(SavedSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(SavedSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(SavedSong("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp))
            add(SavedSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp2))
            add(SavedSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp))
            add(SavedSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp2))
            add(SavedSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(SavedSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(SavedSong("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp))
            add(SavedSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp2))
            add(SavedSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp))
            add(SavedSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp2))
        }

        val savedsongRVAdapter = SavedSongRVAdapter(savedsongDatas)
        binding.lockerSavedSongRv.adapter = savedsongRVAdapter
        binding.lockerSavedSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        savedsongRVAdapter.setMyItemClickListener(object: SavedSongRVAdapter.SavedItemClickListener {
            override fun onRemoveItem(position: Int) {
                savedsongRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }
}