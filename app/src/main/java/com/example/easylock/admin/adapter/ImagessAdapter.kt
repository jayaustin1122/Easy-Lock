import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.easylock.admin.adapter.ImageData
import com.example.easylock.databinding.LogsItemImagesRowBinding

class ImagesAdapter(var imageList: List<ImageData>) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = LogsItemImagesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageData = imageList[position]
        Glide.with(holder.itemView.context)
            .load(imageData.imageUrl)
            .apply(RequestOptions().centerCrop())
            .into(holder.binding.imagesss)
        holder.binding.text.text = imageData.date
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageViewHolder(val binding: LogsItemImagesRowBinding) : RecyclerView.ViewHolder(binding.root)
}
