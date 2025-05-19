import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a3week.Album
import com.example.a3week.databinding.ItemAlbumBinding

class AlbumRVAdapter(private val albumList: ArrayList<Album>) :
    RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(album: Album)
        fun onRemoveAlbum(position: Int)
        fun onPlayClick(album: Album)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun addItem(album: Album) {
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        albumList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAlbumBinding =
            ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albumList[position]
        holder.bind(album)
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(private val binding: ItemAlbumBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            album.coverImage?.let {
                binding.itemAlbumCoverImgIv.setImageResource(it)
            }

            // 앨범 전체 클릭
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(album)
            }

            // 재생 버튼 클릭
            binding.itemAlbumPlayImgIv.setOnClickListener {
                Log.d("AlbumRVAdapter", "Play clicked for ${album.title}")
                itemClickListener.onPlayClick(album)
            }
        }
    }
}
