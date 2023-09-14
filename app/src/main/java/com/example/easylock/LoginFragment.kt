package com.example.easylock

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.easylock.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    private var backPressTime = 0L
    private var doubleBackToExitPressedOnce = false
    private val handler = Handler()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this.requireContext())
        progressDialog.setTitle("PLease wait")
        progressDialog.setCanceledOnTouchOutside(false)
        handler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        binding.tvCreate.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.btnLogin.setOnClickListener {
            validateData()
        }
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
    var email = ""
    var pass = ""

    private fun validateData() {
        email = binding.etUsernameLogin.text.toString().trim()
        pass = binding.etPasswordLogin.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email
            Toast.makeText(this.requireContext(),"Email Invalid", Toast.LENGTH_SHORT).show()
        }
        else if (pass.isEmpty()){

        }
        else{
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In...")
        progressDialog.show()


        auth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this.requireContext(),"Login Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setTitle("Checking user...")

        val firebaseUser = auth.currentUser!!

        val dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val userType = snapshot.child("userType").value

                    if (userType == "member") {
                        Toast.makeText(this@LoginFragment.requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_userFragment)

                    } else if (userType == "admin") {
                        Toast.makeText(this@LoginFragment.requireContext(), "Welcome Admin", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}