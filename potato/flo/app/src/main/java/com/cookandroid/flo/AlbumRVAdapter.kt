package com.cookandroid.flo

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.cookandroid.flo.databinding.ItemAlbumBinding

class AlbumRVAdapter(private val albumList: ArrayList<Album>) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    interface MyItemClickListener{
        fun onItemClick(album: Album)
        fun onRemoveAlbum(position: Int)
        fun onPlayClick(album: Album)  // 버튼 클릭 이벤트 추가.
    }

    private lateinit var myItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        myItemClickListener = itemClickListener
    }

    //앨범 데이터를 받아서 리스트 추가
    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumRVAdapter.ViewHolder {
       //뷰 홀더를 생성해줌. 호출되는 함수.
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
        //사용하는 아이템 객체를 만드는 것이 중요!
    }

    override fun onBindViewHolder(holder: AlbumRVAdapter.ViewHolder, position: Int) {
        //매번 리스트를 받아옴?
        holder.bind(albumList[position])
        //holder.itemView.setOnClickListener{ myItemClickListener.onItemClick(albumList[position])
        //타이틀이 클릭되었을 때,
        //holder.binding.itemAlbumTitleTv.setOnClickListener{
           // myItemClickListener.onRemoveAlbum(position)



    }

    override fun getItemCount(): Int = albumList.size
        //데이터 세트 크기를 알려줌.


    inner class ViewHolder(val binding: ItemAlbumBinding) :  RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album){
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverImgIv.setImageResource(album.coverImg!!)

            // 앨범 전체 클릭
            binding.root.setOnClickListener {
                myItemClickListener.onItemClick(album)
            }

            // 재생 버튼 클릭
            binding.itemAlbumPlayImgIv.setOnClickListener {
                Log.d("AlbumRVAdapter", "Play clicked for ${album.title}")
                myItemClickListener.onPlayClick(album)
            }

        }

    }


}