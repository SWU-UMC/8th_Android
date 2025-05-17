package com.cookandroid.flo

data class Firebase(
    var id: Int = 0,  // Firebase에는 id 필드가 없어도 되지만, 앱에서 유지하려면 필요함
    var title: String = "",
    var singer: String = "",
    var second: Int = 0,
    var playtime: Int = 0,
    var albumIdx: Int = 0,
    var isPlaying: Boolean = false,
    var music: String = "",
    var isLike: Boolean = false
)
