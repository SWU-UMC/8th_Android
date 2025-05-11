package com.cookandroid.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.flo.databinding.SaveItemSongBinding

class SaveSongRVAdapter(private val songList: ArrayList<SaveSong>) : RecyclerView.Adapter<SaveSongRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(song: SaveSong)
        fun onRemoveSong(position: Int)
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


        // [...] 버튼 클릭 시 → 삭제
        holder.binding.songMenuBtn.setOnClickListener {
            myItemClickListener.onRemoveSong(position)
        }
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
            SaveSong(it.title, it.singer, it.coverImg ?: 0, false)
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

