package com.cookandroid.flo


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.flo.data.entities.Song
import com.cookandroid.flo.databinding.ItemAlbumBinding

//과제 요구 범위를 범음! 잠시 중단.
class AlbumSongRVAdapter(private val songList: List<Song>) : RecyclerView.Adapter<AlbumSongRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(song: Song)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    inner class ViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.itemAlbumTitleTv.text = song.title
            binding.itemAlbumSingerTv.text = song.singer

            binding.root.setOnClickListener {
                mItemClickListener.onItemClick(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    override fun getItemCount(): Int = songList.size
}