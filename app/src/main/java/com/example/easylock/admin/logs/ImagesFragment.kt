import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easylock.admin.adapter.ImageData
import com.example.easylock.databinding.FragmentImagesBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class ImagesFragment : Fragment() {
    private lateinit var binding: FragmentImagesBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var storageReference: StorageReference
    private lateinit var imageList: MutableList<ImageData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageReference = FirebaseStorage.getInstance().reference

        // Initialize RecyclerView
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        imagesAdapter = ImagesAdapter(emptyList())
        binding.recycler.adapter = imagesAdapter

        // Fetch images from Firebase Storage
        fetchImages()

        // Setup search functionality
        binding.etSearch2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterImages(s.toString())
            }
        })
    }

    private fun fetchImages() {
        // Retrieve images from Firebase Storage and populate the adapter
        imageList = mutableListOf()
        storageReference.listAll().addOnSuccessListener { result ->
            result.items.forEach { imageRef ->
                imageRef.metadata.addOnSuccessListener { metadata ->
                    // Extracting creation time from metadata
                    val creationTimeMillis = metadata.creationTimeMillis
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
                    val date = dateFormat.format(Date(creationTimeMillis))

                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageData = ImageData(uri.toString(), date)
                        imageList.add(imageData)
                        imagesAdapter = ImagesAdapter(imageList)
                        binding.recycler.adapter = imagesAdapter
                    }
                }
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
        }
    }

    private fun filterImages(query: String) {
        val filteredList = mutableListOf<ImageData>()
        imageList.forEach { imageData ->
            if (imageData.date.contains(query, ignoreCase = true)) {
                filteredList.add(imageData)
            }
        }
        imagesAdapter.imageList = filteredList
        imagesAdapter.notifyDataSetChanged()
    }
}
