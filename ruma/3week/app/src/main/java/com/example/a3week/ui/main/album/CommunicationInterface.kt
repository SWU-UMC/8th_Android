package com.example.a3week.ui.main.album

import com.example.a3week.data.entities.Album

interface CommunicationInterface {
    fun sendData(album: Album)
}