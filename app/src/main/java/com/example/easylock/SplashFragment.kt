package com.example.easylock

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashFragment : Fragment() {
    lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            if(onBoardingFinished()){
                checkUser()
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        },6000)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
    private fun onBoardingFinished():Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)
    }
    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
        else{

            val dbref = FirebaseDatabase.getInstance().getReference("Users")
            dbref.child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val userType = snapshot.child("userType").value

                        if (userType == "admin") {
                            Toast.makeText(this@SplashFragment.requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_splashFragment_to_adminFragment)

                        } else if (userType == "member") {
                            findNavController().navigate(R.id.action_splashFragment_to_userFragment)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }
    }
}