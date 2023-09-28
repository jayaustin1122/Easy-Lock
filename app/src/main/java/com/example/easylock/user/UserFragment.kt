package com.example.easylock.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.easylock.R
import com.example.easylock.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserFragment : Fragment() {
    private lateinit var binding : FragmentUserBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        checkUser()
        loadUsersInfo()
        binding.mainProfile.setOnClickListener {
            findNavController().apply {
                navigate(R.id.editAccountsFragment)
            }
        }
        binding.btnLogoutUser.setOnClickListener {
            auth.signOut()
            findNavController().apply {
                popBackStack(R.id.homeFragment, false) // Pop all fragments up to HomeFragment
                navigate(R.id.loginFragment) // Navigate to LoginFragment

            }
        }
    }
    private fun checkUser() {

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            findNavController().navigate(R.id.action_userFragment_to_loginFragment)
            Toast.makeText(this@UserFragment.requireContext(),"   ", Toast.LENGTH_SHORT).show()
        }
        else{
            val email = firebaseUser.email
        }
    }
    private fun loadUsersInfo() {
        //reference
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get user info
                    val address = "${snapshot.child("address").value}"
                    val currentDate = "${snapshot.child("currentDate").value}"
                    val currentTime = "${snapshot.child("currentTime").value}"
                    val email = "${snapshot.child("email").value}"
                    val fullName = "${snapshot.child("fullName").value}"
                    val id = "${snapshot.child("id").value}"
                    val image = "${snapshot.child("image").value}"
                    val password = "${snapshot.child("password").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    //set data
                    binding.tvName.text = fullName
                    binding.textView5.text = userType

                    Glide.with(requireActivity())
                        .load(image)
                        .into(binding.mainProfile)


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}