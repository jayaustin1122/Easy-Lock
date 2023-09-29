package com.example.easylock.viewpager

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easylock.R
import com.example.easylock.databinding.FragmentThirdBinding

class ThirdFragment : Fragment() {
    private lateinit var progressDialog : ProgressDialog
    private lateinit var binding : FragmentThirdBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(this.requireContext())
        progressDialog.setTitle("PLease wait")
        progressDialog.setCanceledOnTouchOutside(false)
        binding.button1.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            progressDialog.show()
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().apply {
                    navigate(R.id.loginFragment) // Navigate to LoginFragment
                }
                progressDialog.dismiss()
                onBoardingFinish()
                binding.progressBar.visibility = View.INVISIBLE
            }, 3000)

        }
    }
    private fun onBoardingFinish(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }
}