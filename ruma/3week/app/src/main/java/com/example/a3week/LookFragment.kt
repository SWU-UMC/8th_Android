package com.example.a3week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a3week.databinding.FragmentLookBinding

class LookFragment : Fragment() {

    private lateinit var binding: FragmentLookBinding
    private lateinit var scrollView: ScrollView

    private lateinit var buttonList: List<Button>
    private lateinit var textList: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLookBinding.inflate(inflater, container, false)

        scrollView = binding.lookSv

        // 버튼 그룹 초기화
        buttonList = listOf(
            binding.lookChartBtn,
            binding.lookVideoBtn,
            binding.lookGenreBtn,
            binding.lookSituationBtn,
            binding.lookAudioBtn,
            binding.lookAtmostphereBtn
        )

        // 텍스트 그룹 초기화 (차트까지만 사용)
        textList = listOf(
            binding.lookChartTv,
            binding.lookVideoTv,
            binding.lookGenreTv,
            binding.lookSituationTv,
            binding.lookAudioTv,
            binding.lookAtmostphereTv
        )

        setButtonClickListeners()
        initRecyclerView()

        return binding.root
    }

    private fun setButtonClickListeners() {
        for (i in buttonList.indices) {
            val button = buttonList[i]
            button.setOnClickListener {
                setSelectedButton(i)
                scrollView.smoothScrollTo(0, textList[i].top)
            }
        }
    }

    private fun setSelectedButton(index: Int) {
        for (i in buttonList.indices) {
            if (i == index) {
                buttonList[i].setBackgroundResource(R.drawable.selected_button)
            } else {
                buttonList[i].setBackgroundResource(R.drawable.not_selected_button)
            }
        }
    }

    private fun initRecyclerView() {
        val adapter = ChartAdapter(getDummyItems())
        binding.lookChartSongRv.layoutManager = LinearLayoutManager(requireContext())
        binding.lookChartSongRv.adapter = adapter
    }

    private fun getDummyItems(): List<String> {
        return List(10) { "Item $it" }
    }
}
