//package com.example.a3week
//
//import android.content.Context
//import android.util.Log
//import android.widget.Toast
//import com.google.firebase.database.FirebaseDatabase
//
//object FirebaseManager {
//    private const val userId = "user123" // 로그인 연동 전까지 고정
//
//    fun saveSongToFirebase(context: Context, song: Song) {
//        val database = FirebaseDatabase.getInstance()
//        val ref = database.getReference("likedSongs").child(userId).child(song.id.toString())
//
//        val songData = hashMapOf(
//            "id" to song.id,
//            "title" to song.title,
//            "singer" to song.singer,
//            "music" to song.music,
//            "isLike" to true,
//            "albumIdx" to song.albumIdx,
//            "playTime" to song.playTime,
//            "second" to song.second,
//            "isPlaying" to song.isPlaying,
//            "coverImg" to song.coverImg
//        )
//
//        ref.setValue(songData).addOnSuccessListener {
//            Log.d("FirebaseSave", "Saved to Firebase")
//            Toast.makeText(context, "Firebase에 저장됨", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Log.e("FirebaseSave", "Failed to save: ${it.message}")
//            Toast.makeText(context, "Firebase 저장 실패", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun saveAlbumToFirebase(context: Context, album: Album) {
//        val ref = FirebaseDatabase.getInstance()
//            .getReference("albums").child(album.id.toString())
//
//        val albumData = hashMapOf(
//            "id" to album.id,
//            "title" to album.title,
//            "singer" to album.singer,
//            "coverImage" to album.coverImage,
//            "music" to album.music
//        )
//
//        ref.setValue(albumData).addOnSuccessListener {
//            Toast.makeText(context, "앨범 Firebase에 저장됨", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(context, "앨범 저장 실패", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun loadAlbumsFromFirebase(onAlbumsLoaded: (List<Album>) -> Unit) {
//        val ref = FirebaseDatabase.getInstance().getReference("albums")
//
//        ref.get().addOnSuccessListener { dataSnapshot ->
//            val albumList = mutableListOf<Album>()
//            for (child in dataSnapshot.children) {
//                val album = child.getValue(Album::class.java)
//                album?.let { albumList.add(it) }
//            }
//            onAlbumsLoaded(albumList)
//        }.addOnFailureListener {
//            // 실패 처리 (필요 시 로그)
//        }
//    }
//
//    fun loadLikedSongs(onSongsLoaded: (List<Song>) -> Unit) {
//        val ref = FirebaseDatabase.getInstance().getReference("likedSongs").child(userId)
//
//        ref.get().addOnSuccessListener { dataSnapshot ->
//            val songList = mutableListOf<Song>()
//            for (child in dataSnapshot.children) {
//                val song = child.getValue(Song::class.java)
//                song?.let { songList.add(it) }
//            }
//            onSongsLoaded(songList)
//        }
//    }
//
//    fun unlikeAllSongs(context: Context) {
//        val ref = FirebaseDatabase.getInstance().getReference("likedSongs").child(userId)
//
//        ref.removeValue().addOnSuccessListener {
//            Toast.makeText(context, "모든 좋아요 삭제됨", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun removeSongFromFirebase(context: Context, songId: Int) {
//        val ref = FirebaseDatabase.getInstance()
//            .getReference("likedSongs").child(userId).child(songId.toString())
//
//        ref.removeValue().addOnSuccessListener {
//            Toast.makeText(context, "좋아요 취소됨", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(context, "취소 실패", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//}