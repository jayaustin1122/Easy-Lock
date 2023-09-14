package com.example.easylock

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.easylock.databinding.FragmentSignUpBinding
import com.example.easylock.model.AccountModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


class SignUpFragment : Fragment() {
    private lateinit var binding : FragmentSignUpBinding
    private lateinit var progressDialog : ProgressDialog
    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var database : FirebaseDatabase
    private lateinit var selectedImage : Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        progressDialog = ProgressDialog(this.requireContext())
        progressDialog.setTitle("PLease wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.imageView2.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        binding.btnSignUp.setOnClickListener {
            validateData()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if (data.data != null){
                selectedImage = data.data!!
                binding.imageView2.setImageURI(selectedImage)
            }
        }
    }
    private var email = ""
    private var pass = ""
    private var fullname = ""
    private var address = ""
    private var userType = "member"

    private fun validateData() {
        val email = binding.etEmailSignUp.text.toString().trim()
        val pass = binding.etPasswordSignUp.text.toString().trim()
        val fullname = binding.etFullname.text.toString().trim()
        val address = binding.etPasscode.text.toString().trim()

        when {
            email.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Email...", Toast.LENGTH_SHORT).show()
            pass.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Password...", Toast.LENGTH_SHORT).show()
            fullname.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Fullname...", Toast.LENGTH_SHORT).show()
            address.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Address...", Toast.LENGTH_SHORT).show()
            else -> createUserAccount()
        }
    }
    private fun createUserAccount() {
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(binding.etEmailSignUp.text.toString().trim(),binding.etPasswordSignUp.text.toString().trim())

            .addOnSuccessListener {
                // if user successfully created ()
                getRFIDDataAndUploadImage()
            }
            .addOnFailureListener { e ->
                //if the user fialef creating account
                progressDialog.dismiss()
                Toast.makeText(this.requireContext(),"Failed Creating Account or ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
    private fun getRFIDDataAndUploadImage() {
        val uid = auth.uid
        val userRef = database.getReference("RFIDData") // Change to your actual path

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rfidData = dataSnapshot.getValue(String::class.java)
                if (rfidData != null) {
                    uploadImage(rfidData)
                } else {
                    // Handle the case where RFID data is not available
                    Toast.makeText(this@SignUpFragment.requireContext(), "RFID data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "getRFIDData:onCancelled", databaseError.toException())
                // Handle the case where an error occurred while retrieving RFID data
                Toast.makeText(this@SignUpFragment.requireContext(), "Error retrieving RFID data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadImage(rfidData: String,) {
        progressDialog.setMessage("Uploading Image...")
        progressDialog.show()
        val uid = auth.uid

        val reference = storage.reference.child("profile")
            .child(uid!!)
        reference.putFile(selectedImage).addOnCompleteListener{
            if (it.isSuccessful){
                reference.downloadUrl.addOnSuccessListener {task->
                    // Pass the RFID data to uploadInfo
                    uploadInfo(task.toString(), rfidData)
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(this@SignUpFragment.requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun uploadInfo(imageUrl: String, rfidData: String) {
        progressDialog.setMessage("Saving Account...")
        progressDialog.show()
        email = binding.etEmailSignUp.text.toString().trim()
        pass = binding.etPasswordSignUp.text.toString().trim()
        fullname = binding.etFullname.text.toString().trim()
        address = binding.etPasscode.text.toString().trim()
        val currentDate = getCurrentDate()
        val currentTime = getCurrentTime()
        val timestamp = System.currentTimeMillis()
        val uid = auth.uid

        val user = AccountModel(
            uid = uid,
            email = email,
            password = pass,
            fullName = fullname,
            address = address,
            image = imageUrl,
            currentDate = currentDate,
            currentTime = currentTime,
            id = "$timestamp",
            userType = "member",
            RFID = rfidData
        )

        try {
            database.getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(user)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        progressDialog.dismiss()
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                        Toast.makeText(this.requireContext(),"Account Created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this.requireContext(), task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            // Handle any exceptions that might occur during the upload process.
            progressDialog.dismiss()
            Toast.makeText(this.requireContext(), "Error uploading data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getCurrentTime(): String {
        val tz = TimeZone.getTimeZone("GMT+08:00")
        val c = Calendar.getInstance(tz)
        val hours = String.format("%02d", c.get(Calendar.HOUR))
        val minutes = String.format("%02d", c.get(Calendar.MINUTE))
        return "$hours:$minutes"
    }


    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val currentDateObject = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        return formatter.format(currentDateObject)
    }
}