package com.example.easylock

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

object FragmentUtils {
    fun navigateToFragment(
        fragmentManager: FragmentManager,
        containerId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true
    ) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(containerId, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}