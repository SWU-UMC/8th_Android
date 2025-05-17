package com.cookandroid.flo

data class SaveSong(
    var title: String = "",
    var singer: String = "",
    var coverImg: Int = 0,
    var isChecked: Boolean = false, // 스위치 상태 저장
    //스크롤 시 스위치가 켜지거나 꺼지는 이상한 동작을 방지하기 위함.
    var isLike: Boolean = false,
    val id: Int = 0,//id 보관

    // Firebase용 확장 필드 추가
    var music: String = "",         // 음악 파일명
    var playtime: Int = 60,         // 재생 시간 (초)
    var isPlaying: Boolean = false, // 재생 중인지 여부
    var second: Int = 0             // 재생된 시간
)
