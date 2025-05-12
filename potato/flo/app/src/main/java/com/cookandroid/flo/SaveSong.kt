package com.cookandroid.flo

data class SaveSong(
    var title: String = "",
    var singer: String = "",
    var coverImg: Int = 0,
    var isChecked: Boolean = false, // 스위치 상태 저장
    //스크롤 시 스위치가 켜지거나 꺼지는 이상한 동작을 방지하기 위함.

    val id: Int = 0 //id 보관
)
