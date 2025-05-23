package com.cookandroid.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.flo.databinding.SaveItemSongBinding

class SaveSongRVAdapter(val songList : ArrayList<SaveSong>) : RecyclerView.Adapter<SaveSongRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(song: SaveSong)
        fun onRemoveSong(songId: Int)
    }

    private lateinit var myItemClickListener: MyItemClickListener



    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        myItemClickListener = itemClickListener
    }

    fun addItem(song: SaveSong) {
        songList.add(song)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        songList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SaveItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songList[position]  // song을 정의합니다.

        holder.bind(song) //피드백 반영

        //holder.bind(songList[position])

        //피드백 반영 수정!
        holder.itemView.setOnClickListener {
            myItemClickListener.onItemClick(song)
        }

        // 스위치 리스너 초기화 (필수!)
        holder.binding.songSwitch.setOnCheckedChangeListener(null)

        // 스위치 상태 반영
        holder.binding.songSwitch.isChecked = song.isChecked

        // 스위치 상태 변경 시 데이터도 변경
        holder.binding.songSwitch.setOnCheckedChangeListener { _, isChecked ->
            song.isChecked = isChecked
        }
        //어댑터 삭제 버튼 클릭 처리!
        holder.binding.songDeleteBtn.setOnClickListener {
            myItemClickListener.onRemoveSong(song.id)
        }


        // [...] 버튼 클릭 시 → 삭제 => 지난 시간 과제. 밑에 새로 이번주차 강의 반영해서 수정.
//        holder.binding.songMenuBtn.setOnClickListener {
//            myItemClickListener.onRemoveSong(position)
//        }
       /* holder.binding.itemSongMoreIv.setOnClickListener{
            //myItemClickListener.onRemoveSong(songs[position].id)
            myItemClickListener.onRemoveSong(song.id)
            //removeSong(position)
        }*/



    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding: SaveItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SaveSong) {
            binding.songTitleTv.text = song.title
            binding.songSingerTv.text = song.singer
            binding.songAlbumIv.setImageResource(song.coverImg)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<SaveSong>) {
        this.songList.clear()
        this.songList.addAll(songs.map {
            SaveSong(
                it.title,
                it.singer,
                it.coverImg,
                false,       // isChecked 초기값 (스크롤 시 스위치 이상 방지)
                it.isLike,   // 좋아요 상태 유지
                it.id
            )
        })
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeSong(position: Int) {
        songList.removeAt(position)
        notifyDataSetChanged()

    }

//    inner class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(song: Song) {
//            binding.itemSongImgTv.setImageResource(song.coverImg!!)
//            binding.itemSongTitleTv.text = song.title
//            binding.itemSongSingerTv.text = song.singer
//        }
//    }
}

