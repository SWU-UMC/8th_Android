package com.example.workbook4

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workbook4.databinding.ItemSavedSongBinding

class SavedSongRVAdapter(private val albumList: ArrayList<SavedSong>): RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {
    interface SavedItemClickListener {
        fun onRemoveItem(position: Int)
    }

    private lateinit var savedItemClickListener: SavedItemClickListener
    fun setMyItemClickListener(itemClickListener: SavedItemClickListener) {
        savedItemClickListener = itemClickListener
    }

    fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SavedSongRVAdapter.ViewHolder {
        val binding: ItemSavedSongBinding = ItemSavedSongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedSongRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.binding.itemSavedSongSwitch.setOnCheckedChangeListener(null)
        holder.binding.itemSavedSongSwitch.isChecked = albumList[position].isSelected
        holder.binding.itemSavedSongSwitch.setOnCheckedChangeListener { _, isChecked ->
            albumList[position].isSelected = isChecked
        }
        holder.binding.itemSavedSongMoreIv.setOnClickListener { savedItemClickListener.onRemoveItem(position) }
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemSavedSongBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(savedsong: SavedSong){
            binding.itemSavedSongAlbumTitleTv.text = savedsong.title
            binding.itemSavedSongAlbumSingerTv.text = savedsong.singer
            binding.itemSavedSongAlbumCoverIv.setImageResource(savedsong.coverImg!!)
        }
    }
}