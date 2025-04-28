package com.example.workbook4

data class Song(
    val title : String = "",
    val singer : String = "",
    var second: Int = 0,
    var playTie: Int = 0,
    var isPlaying: Boolean = false,
    var music: String = ""
)