package com.example.a3week.ui.main.locker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a3week.data.entities.Album
import com.example.a3week.databinding.ItemLockerAlbumBinding

class LockerAlbumRVAdapter (private val albumList: ArrayList<Album>) : RecyclerView.Adapter<LockerAlbumRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(album: Album)
        fun onRemoveAlbum(position: Int) // 추가된 함수
    }

    private lateinit var itemClickListener : OnItemClickListener
    val switchStatus = mutableMapOf<Int, Boolean>()

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemLockerAlbumBinding = ItemLockerAlbumBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(albumList[position])
        }

        holder.binding.itemLockerAlbumMoreIv.setOnClickListener {
            itemClickListener.onRemoveAlbum(position)
        }

        val switch =  holder.binding.switchRV
        switch.isChecked = switchStatus.getOrDefault(position, false) // NullPointerException 방지
        switch.setOnClickListener {
            switchStatus[position] = switch.isChecked // Map에 현재 상태 업데이트
        }
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemLockerAlbumBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.itemLockerAlbumTitleTv.text = album.title
            binding.itemLockerAlbumSingerTv.text = album.singer
            album.coverImage?.let { binding.itemLockerAlbumCoverImgIv.setImageResource(it) }
        }
    }

    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }
}