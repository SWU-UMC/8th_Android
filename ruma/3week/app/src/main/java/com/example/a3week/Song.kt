package com.example.a3week

data class Song(
    val title : String = "",
    val singer : String = "",
    val second:Int=0,
    var playTime:Int=0,
    var isPlaying: Boolean = false,
    var music : String ="music_lilac"
)
