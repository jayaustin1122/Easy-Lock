import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easylock.admin.adapter.ViewPagerAdapter
import com.example.easylock.admin.logs.LogsFragment
import com.example.easylock.databinding.FragmentLogsAdminBinding


class LogsAdminFragment : Fragment() {
    private lateinit var binding: FragmentLogsAdminBinding
    private var selectedTabIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogsAdminBinding.inflate(layoutInflater)

        // Restore the selected tab index from the saved instance state
        savedInstanceState?.let {
            selectedTabIndex = it.getInt("selectedTabIndex", 0)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(LogsFragment(), "Logs")
        adapter.addFragment(ImagesFragment(), "Unauthorized Access")

        binding.viewPager.adapter = adapter
        binding.tbLayout.setupWithViewPager(binding.viewPager)
        // Set the selected tab based on the restored index
        binding.viewPager.currentItem = selectedTabIndex
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the selected tab index to restore it later
        outState.putInt("selectedTabIndex", binding.viewPager.currentItem)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Restore the selected tab index from the saved instance state
        savedInstanceState?.let {
            selectedTabIndex = it.getInt("selectedTabIndex", 0)
        }
    }

}
