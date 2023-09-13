package com.example.easylock.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.easylock.R
import com.example.easylock.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {


    private lateinit var binding : FragmentSecondBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager  = activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.button1.setOnClickListener {
            viewPager?.currentItem = 2
        }
    }

}