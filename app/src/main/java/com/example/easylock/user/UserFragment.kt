package com.example.easylock.user

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.easylock.R
import com.example.easylock.admin.adapter.LogsAdapter
import com.example.easylock.admin.tab.accounts.EditAccountsFragment
import com.example.easylock.databinding.FragmentUserBinding
import com.example.easylock.model.LogsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserFragment : Fragment() {
    private lateinit var binding : FragmentUserBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var logsArrayList : ArrayList<LogsModel>
    private var backPressTime = 0L
    private var doubleBackToExitPressedOnce = false
    private val handler = Handler()
    //adapter
    private lateinit var adapter : MyLogsAdapter
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
        getLogs()
        binding.mainProfile.setOnClickListener {
            val fullName = binding.tvName.toString()
            val image = binding.mainProfile.toString()

            val bundle = Bundle()
            bundle.putString("fullname", fullName)
            bundle.putString("image", image)


            val editFragment = EditAccountsFragment()
            editFragment.arguments = bundle

            // Now, replace the current fragment with the EditAccountFragment
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, EditUserFragment()) // Replace R.id.fragment_container with your actual container ID
                .addToBackStack(null)
                .commit()
        }
        binding.btnLogoutUser.setOnClickListener {
            auth.signOut()
            findNavController().apply {
                popBackStack(R.id.homeFragment, false) // Pop all fragments up to HomeFragment
                navigate(R.id.loginFragment) // Navigate to LoginFragment

            }
        }
        binding.pinBtn.setOnClickListener {
            findNavController().apply {
                navigate(R.id.userPinragment) // Navigate to LoginFragment

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
    private fun getLogs() {
        //initialize
        logsArrayList = ArrayList()

        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser!!.uid)
            .child("MyLogs")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear list
                logsArrayList.clear()
                for (data in snapshot.children) {
                    //data as model
                    val model = data.getValue(LogsModel::class.java)

                    // add to array
                    model?.let {
                        logsArrayList.add(it)
                    }
                }
                //set up adapter
                adapter = MyLogsAdapter(this@UserFragment, logsArrayList)
                binding.recy.setHasFixedSize(true)
                binding.recy.layoutManager = LinearLayoutManager(context).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                binding.recy.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onPause() {
        callback.remove()
        super.onPause()
    }
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                requireActivity().finish()
            } else {
                doubleBackToExitPressedOnce = true
                Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
                handler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }
    }
}