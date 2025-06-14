package com.example.a3week.ui.main.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.a3week.databinding.FragmentBottomSheetBinding
import com.example.a3week.ui.song.SongDatabase
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding : FragmentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.bottomSheetIv1.setOnClickListener {
            Toast.makeText(requireActivity(), "듣기 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.bottomSheetIv2.setOnClickListener {
            Toast.makeText(requireActivity(), "재생목록 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.bottomSheetIv3.setOnClickListener {
            Toast.makeText(requireActivity(), "내 리스트 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.bottomSheetIv4.setOnClickListener {
            val songDB = SongDatabase.Companion.getInstance(requireContext())
            songDB.songDao().unlikeAll()
            songDB.songDao().deleteUnlikedSongs()

            // 저장한 곡 탭 새로고침 시도
            val fragment = parentFragmentManager.findFragmentByTag("f0") as? SavedSongFragment
            fragment?.let {
                it.refreshSongList()
            }

            Toast.makeText(requireContext(), "전체 삭제 완료", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}