package com.cookandroid.flo
// Firebase import 추가
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.flo.databinding.FragmentSaveBinding
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.cookandroid.flo.databinding.BottomSheetDialogBinding




class SaveFragment : Fragment() {

    lateinit var binding: FragmentSaveBinding
    private lateinit var saveSongRVAdapter: SaveSongRVAdapter
    private val saveSongList = ArrayList<SaveSong>()

    // Firebase 필드 선언
    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser"

    // Firebase 좋아요 저장/삭제 함수 추가
    private fun likeSong(song: SaveSong) {
        val ref = database.getReference("users/$userId/likedSongs/${song.id}")
        ref.setValue(song.copy(isLike = true))
    }

    private fun unlikeSong(songId: Int) {
        val ref = database.getReference("users/$userId/likedSongs/$songId")
        ref.removeValue()
    }

    private fun getLikedSongs(onComplete: (List<SaveSong>) -> Unit) {
        val ref = database.getReference("users/$userId/likedSongs")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SaveSong>()
                for (child in snapshot.children) {
                    val song = child.getValue(SaveSong::class.java)
                    if (song != null) list.add(song)
                }
                onComplete(list)
            }

            override fun onCancelled(error: DatabaseError) {
                context?.let {
                    Toast.makeText(it, "Firebase 오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        // Room DB 초기화
        // songDB = SongDatabase.getInstance(requireContext())!!

        // 샘플 데이터 목록 초기화
        initSaveSongList()

        // RecyclerView 연결
        initRecyclerView()

        return binding.root
    }

    //1. select_all_tv 클릭 시 BottomSheetDialog 띄우기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectAllTv.setOnClickListener {
            showBottomEditBar()
        }
    }

    private fun showBottomEditBar() {
        val dialog = BottomSheetDialog(requireContext())

        // ViewBinding으로 bottom_sheet_dialog.xml 연결
        val bottomSheetBinding = BottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        // 삭제 버튼 클릭
        bottomSheetBinding.editbarAddplayDelete.setOnClickListener {
            deleteAllLikedSongs()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteAllLikedSongs() {
        // DB에서 업데이트
        // songDB.songDao().updateAllIsLikeFalse()

        // Firebase에서 전체 삭제
        database.getReference("users/$userId/likedSongs").removeValue()

        // 리스트 초기화
        saveSongList.clear()
        saveSongRVAdapter.notifyDataSetChanged()

        Toast.makeText(requireContext(), "전체 곡이 삭제되었습니다", Toast.LENGTH_SHORT).show()
    }

    // 저장된 곡 리스트 초기화
    private fun initSaveSongList() {
        // 주석 유지
    }

    private fun showDeleteDialog(songId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("정말 이 곡을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deleteSongWithUndo(songId)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteSongWithUndo(songId: Int) {
        val index = saveSongList.indexOfFirst { it.id == songId }
        if (index == -1) return

        val deletedSong = saveSongList[index]
        saveSongList.removeAt(index)
        saveSongRVAdapter.notifyItemRemoved(index)

        // DB에서 좋아요 해제
        // songDB.songDao().updateIsLikeById(false, songId)

        unlikeSong(songId) // Firebase 삭제

        // 스낵바로 되돌리기 제공
        Snackbar.make(binding.root, "곡이 삭제되었습니다", Snackbar.LENGTH_LONG)
            .setAction("되돌리기") {
                saveSongList.add(index, deletedSong)
                saveSongRVAdapter.notifyItemInserted(index)
                // DB 복구
                // songDB.songDao().updateIsLikeById(true, songId)
                likeSong(deletedSong) // Firebase 복구
            }
            .show()
    }

    // RecyclerView 어댑터 연결
//    private fun initRecyclerView() {
//        getLikedSongs { likedSongs ->
//            saveSongRVAdapter = SaveSongRVAdapter(ArrayList(likedSongs))
//            binding.songRv.adapter = saveSongRVAdapter
//            binding.songRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//
//            // 클릭 이벤트 설정
//            saveSongRVAdapter.setMyItemClickListener(object : SaveSongRVAdapter.MyItemClickListener {
//                override fun onItemClick(song: SaveSong) {
//                    Toast.makeText(requireContext(), "클릭한 곡: ${song.title}", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onRemoveSong(songId: Int) {
//                    unlikeSong(songId)
//                    val index = saveSongRVAdapter.songList.indexOfFirst { it.id == songId }
//                    if (index != -1) {
//                        saveSongRVAdapter.removeItem(index)
//                    }
//                }
//            })
//        }
//    }
    private fun initRecyclerView() {
        getLikedSongs { likedSongs ->
            if (!isAdded || context == null) return@getLikedSongs  // 🔒 context 안전하게 확인

            saveSongRVAdapter = SaveSongRVAdapter(ArrayList(likedSongs))
            binding.songRv.adapter = saveSongRVAdapter
            binding.songRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            saveSongRVAdapter.setMyItemClickListener(object : SaveSongRVAdapter.MyItemClickListener {
                override fun onItemClick(song: SaveSong) {
                    context?.let {
                        Toast.makeText(it, "클릭한 곡: ${song.title}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onRemoveSong(songId: Int) {
                    unlikeSong(songId)
                    val index = saveSongRVAdapter.songList.indexOfFirst { it.id == songId }
                    if (index != -1) {
                        saveSongRVAdapter.removeItem(index)
                    }
                }
            })
        }
    }
}



//이후 혹시 모를 불상사를 대비...(다시 Room을 봐야할 일이 생길 수 있어서 이 부분은 전체 주석 처리했습니다...)

/*
class SaveFragment : Fragment() {

    lateinit var binding: FragmentSaveBinding
    private lateinit var saveSongRVAdapter: SaveSongRVAdapter
    private val saveSongList = ArrayList<SaveSong>()
    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        // Room DB 초기화
        songDB = SongDatabase.getInstance(requireContext())!!
        // 샘플 데이터 목록 초기화
        initSaveSongList()

        // RecyclerView 연결
        initRecyclerView()



        return binding.root
    }

    //1. select_all_tv 클릭 시 BottomSheetDialog 띄우기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectAllTv.setOnClickListener {
            showBottomEditBar()
        }
    }

    private fun showBottomEditBar() {
        val dialog = BottomSheetDialog(requireContext())

        // ViewBinding으로 bottom_sheet_dialog.xml 연결
        val bottomSheetBinding = BottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        // 삭제 버튼 클릭
        bottomSheetBinding.editbarAddplayDelete.setOnClickListener {
            deleteAllLikedSongs()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteAllLikedSongs() {
        // DB에서 업데이트
        songDB.songDao().updateAllIsLikeFalse()

        // 리스트 초기화
        saveSongList.clear()
        saveSongRVAdapter.notifyDataSetChanged()

        Toast.makeText(requireContext(), "전체 곡이 삭제되었습니다", Toast.LENGTH_SHORT).show()
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

    private fun showDeleteDialog(songId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("정말 이 곡을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deleteSongWithUndo(songId)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteSongWithUndo(songId: Int) {
        val index = saveSongList.indexOfFirst { it.id == songId }
        if (index == -1) return

        val deletedSong = saveSongList[index]
        saveSongList.removeAt(index)
        saveSongRVAdapter.notifyItemRemoved(index)

        // DB에서 좋아요 해제
        songDB.songDao().updateIsLikeById(false, songId)

        // 스낵바로 되돌리기 제공
        Snackbar.make(binding.root, "곡이 삭제되었습니다", Snackbar.LENGTH_LONG)
            .setAction("되돌리기") {
                saveSongList.add(index, deletedSong)
                saveSongRVAdapter.notifyItemInserted(index)
                // DB 복구
                songDB.songDao().updateIsLikeById(true, songId)
            }
            .show()
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

            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
                showDeleteDialog(songId)
                // ✅ position 기반 삭제 ❌ → 대신 id로 찾아서 삭제 ✅ -> 리스트 전체 갱신으로 뷰 홀더 포지션 값이 유효하지 않는 문제가 있어서 수정
                val index = saveSongList.indexOfFirst { it.id == songId }
                if (index != -1) {
                    saveSongList.removeAt(index)
                    saveSongRVAdapter.notifyItemRemoved(index)
                }
            }
        })


        // Room DB에서 좋아요된 곡 가져오기
        val songDB = SongDatabase.getInstance(requireContext())!!
        val likedSongs = songDB.songDao().getlikedSong(true)

        // Song → SaveSong 변환 후 추가
        val saveSongs = likedSongs.map {
            SaveSong(it.title, it.singer, it.coverImg ?: 0,false, it.id)
        }

        saveSongRVAdapter.addSongs(ArrayList(saveSongs))
    }
}*/