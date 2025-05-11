package com.cookandroid.flo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.flo.databinding.FragmentSaveBinding

class SaveFragment : Fragment() {

    lateinit var binding: FragmentSaveBinding
    private lateinit var saveSongRVAdapter: SaveSongRVAdapter
    private val saveSongList = ArrayList<SaveSong>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        // 샘플 데이터 목록 초기화
        initSaveSongList()

        // RecyclerView 연결
        initRecyclerView()

        return binding.root
    }

    // 저장된 곡 리스트 초기화
    private fun initSaveSongList() {
//        saveSongList.apply {
//            add(SaveSong("날 봐 귀순", "대성", R.drawable.see_me))
//            add(SaveSong("Extral", "제니", R.drawable.jennie_extral))
//            add(SaveSong("whiplash", "asepa", R.drawable.aespa_whiplash))
//            add(SaveSong("sign", "izna", R.drawable.izna_sign))
//            add(SaveSong("like jennie", "제니", R.drawable.jennie_like_jennie))
//            add(SaveSong("날 봐 귀순", "대성", R.drawable.see_me))
//            add(SaveSong("Extral", "제니", R.drawable.jennie_extral))
//            add(SaveSong("whiplash", "asepa", R.drawable.aespa_whiplash))
//            add(SaveSong("sign", "izna", R.drawable.izna_sign))
//            add(SaveSong("날 봐 귀순", "대성", R.drawable.see_me))
//            add(SaveSong("Extral", "제니", R.drawable.jennie_extral))
//            add(SaveSong("whiplash", "asepa", R.drawable.aespa_whiplash))
//            add(SaveSong("sign", "izna", R.drawable.izna_sign))
//        }

    }

    // RecyclerView 어댑터 연결
    private fun initRecyclerView() {
        saveSongRVAdapter = SaveSongRVAdapter(saveSongList)
        binding.songRv.adapter = saveSongRVAdapter
        binding.songRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 클릭 이벤트 설정
        saveSongRVAdapter.setMyItemClickListener(object : SaveSongRVAdapter.MyItemClickListener {
            override fun onItemClick(song: SaveSong) {
                Toast.makeText(requireContext(), "클릭한 곡: ${song.title}", Toast.LENGTH_SHORT).show()
            }

            override fun onRemoveSong(position: Int) {
                saveSongRVAdapter.removeItem(position)
                Toast.makeText(requireContext(), "삭제된 곡: ${position + 1}", Toast.LENGTH_SHORT).show()
            }
        })
        // ✅ Room DB에서 좋아요된 곡 가져오기
        val songDB = SongDatabase.getInstance(requireContext())!!
        val likedSongs = songDB.songDao().getlikedSong(true)

        // ✅ Song → SaveSong 변환 후 추가
        val saveSongs = likedSongs.map {
            SaveSong(it.title, it.singer, it.coverImg ?: 0)
        }

        saveSongRVAdapter.addSongs(ArrayList(saveSongs))
    }
}