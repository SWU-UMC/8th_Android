package com.cookandroid.two_week
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.two_week.databinding.FragmentListBinding

class ListFragment : Fragment() {

    // 뷰 바인딩 객체 선언
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩 초기화
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root // 바인딩된 root view 반환
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 메모리 누수를 방지하기 위해 onDestroyView에서 binding 해제
        _binding = null
    }
}