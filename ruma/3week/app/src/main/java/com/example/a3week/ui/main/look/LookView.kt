package com.example.a3week.ui.main.look

import com.example.a3week.ui.song.FloChartResult

interface LookView {
    fun onGetSongLoading()
    fun onGetSongSuccess(code: Int, result: FloChartResult)
    fun onGetSongFailure(code: Int, message: String)
}