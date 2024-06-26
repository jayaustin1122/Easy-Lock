package com.example.easylock.admin

import LogsAdminFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.easylock.R
import com.example.easylock.admin.tab.AccountsFragment
import com.example.easylock.admin.tab.HomeFragment
import com.example.easylock.databinding.FragmentAdminBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminBinding
    private lateinit var homeFragment: Fragment
    private lateinit var accountsFragment: Fragment
    private val logsFragment: Fragment by lazy { LogsAdminFragment() } // Lazy initialization for LogsAdminFragment
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentManager = requireActivity().supportFragmentManager
        homeFragment = HomeFragment()
        accountsFragment = AccountsFragment()

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.navigation_home -> homeFragment
                R.id.navigation_logs -> logsFragment
                R.id.navigation_accounts -> accountsFragment
                else -> return@setOnNavigationItemSelectedListener false
            }
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commitAllowingStateLoss() // Use commitAllowingStateLoss() to retain fragment state
            true
        }

        if (savedInstanceState == null) {
            // Initially load the HomeFragment only if it's not already added
            if (!homeFragment.isAdded) {
                fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, homeFragment)
                    .commit()
            }
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }
    }
}
