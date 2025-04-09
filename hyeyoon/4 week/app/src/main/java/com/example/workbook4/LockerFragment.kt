package com.example.workbook4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workbook4.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    lateinit var binding: FragmentLockerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        var lockerVPAdapter = LockerVPAdapter(this)
        binding.lockerViewpager.adapter = lockerVPAdapter

        TabLayoutMediator(binding.lockerTabLayout, binding.lockerViewpager) {
                tab, position ->
            tab.text = when(position) {
                0 -> "저장한 곡"
                else -> "음악파일"
            }
        }.attach()
        return binding.root
    }
}