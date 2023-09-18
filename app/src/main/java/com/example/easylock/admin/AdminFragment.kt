package com.example.easylock.admin


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.easylock.R
import com.example.easylock.admin.tab.AccountsFragment
import com.example.easylock.admin.tab.HomeFragment
import com.example.easylock.admin.tab.LogsAdminFragment
import com.example.easylock.databinding.FragmentAdminBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminFragment : Fragment() {
    private lateinit var binding : FragmentAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val homeFragment: Fragment = HomeFragment()
        val accountsFragment : Fragment = AccountsFragment()
        val logsFragment: Fragment = LogsAdminFragment()

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
                .commit()
            true
        }
        // Initially load the HomeFragment
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homeFragment)
            .commit()
    }
}