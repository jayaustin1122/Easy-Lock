import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easylock.R
import com.example.easylock.admin.adapter.ViewPagerAdapter
import com.example.easylock.admin.logs.LogsFragment
import com.example.easylock.databinding.FragmentLogsAdminBinding
import com.google.android.material.tabs.TabLayout

class LogsAdminFragment : Fragment() {
    private lateinit var binding: FragmentLogsAdminBinding
    private var selectedTabIndex: Int = 0
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogsAdminBinding.inflate(inflater, container, false)
        tabLayout = binding.tbLayout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore the selected tab index from the saved instance state
        selectedTabIndex = savedInstanceState?.getInt("selectedTabIndex", 0) ?: 0

        // Show the initial fragment
        showFragment(selectedTabIndex)
        addTabs()
        binding.tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    showFragment(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the selected tab index to restore it later
        outState.putInt("selectedTabIndex", selectedTabIndex)
    }

    private fun showFragment(position: Int) {
        val fragment = when (position) {
            0 -> LogsFragment()
            1 -> ImagesFragment()
            else -> LogsFragment() // Default to LogsFragment
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.viewPager, fragment)
            .commit()
        selectedTabIndex = position
    }
    private fun addTabs() {
        // Add tab items dynamically
        tabLayout.addTab(tabLayout.newTab().setText("Logs"))
        tabLayout.addTab(tabLayout.newTab().setText("Unauthorized Access"))
    }
}