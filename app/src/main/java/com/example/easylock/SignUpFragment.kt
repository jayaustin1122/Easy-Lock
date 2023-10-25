package com.example.easylock

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.easylock.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    private val handler = Handler()

    private val fetchRFIDDataRunnable = object : Runnable {
        override fun run() {
            getRFIDData()
            handler.postDelayed(this, 5000) // Fetch data every minute (60000 milliseconds)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(fetchRFIDDataRunnable) // Stop fetching when the fragment is destroyed
    }
    private fun getRFIDData() {
        val uid = auth.uid
        val userRef = database.getReference("RFID") // Change to your actual path

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rfidData = dataSnapshot.getValue(String::class.java)
                if (rfidData != null) {
                    binding.etRfid.setText(rfidData)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        progressDialog = ProgressDialog(this.requireContext())
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        handler.post(fetchRFIDDataRunnable)
        binding.imageView2.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)
        }
        binding.btnBack.setOnClickListener {
            database.getReference("Register").setValue("False")
            findNavController().apply {
                navigate(R.id.loginFragment) // Navigate to LoginFragment
            }
        }
        binding.btnSignUp.setOnClickListener {
            validateData()

        }

    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (data.data != null) {
                    selectedImage = data.data!!
                    binding.imageView2.setImageURI(selectedImage)
                }
            }
        }
    }
    private var email = ""
    private var pass = ""
    private var fullname = ""
    private var pinCode = ""
    private var userType = "member"
    private var rfidData = ""
    private var pin = ""

    private fun validateData() {
        val email = binding.etEmailSignUp.text.toString().trim()
        val pass = binding.etPasswordSignUp.text.toString().trim()
        val fullname = binding.etFullname.text.toString().trim()
        val pinCode = binding.etPasscode.text.toString().trim()
        val rfid = binding.etRfid.text.toString().trim()

        when {
            email.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Email...", Toast.LENGTH_SHORT).show()
            pass.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Password...", Toast.LENGTH_SHORT).show()
            fullname.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Name...", Toast.LENGTH_SHORT).show()
            pinCode.isEmpty() -> Toast.makeText(this.requireContext(), "Enter Your Pin Code...", Toast.LENGTH_SHORT).show()
            rfid.isEmpty()-> Toast.makeText(this.requireContext(),"Tap Your Card",Toast.LENGTH_SHORT).show()
            !::selectedImage.isInitialized -> Toast.makeText(this.requireContext(),"Please Upload a Picture",Toast.LENGTH_SHORT).show()
            else -> createUserAccount()
        }
    }



    private fun createUserAccount() {
        val email = binding.etEmailSignUp.text.toString()
        val password = binding.etPasswordSignUp.text.toString()
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(email,password).await()
                withContext(Dispatchers.Main){
                    getRFIDDataAndUploadImage()
                }

            }
            catch (e : Exception){
                withContext(Dispatchers.Main){
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@SignUpFragment.requireContext(),
                        "Failed Creating Account or ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
    private fun getRFIDDataAndUploadImage() {
        val uid = auth.uid
        val userRef = database.getReference("RFID") // Change to your actual path

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

    private fun uploadImage(rfidData: String) {
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
                    uploadInfo2()
                    uploadInfo3()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(this@SignUpFragment.requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun uploadInfo(imageUrl: String,rfidData: String) {
    progressDialog.setMessage("Saving Account...")
    progressDialog.show()
    email = binding.etEmailSignUp.text.toString().trim()
    pass = binding.etPasswordSignUp.text.toString().trim()
    fullname = binding.etFullname.text.toString().trim()
    this.rfidData = binding.etRfid.text.toString().trim()
    pin = binding.etPasscode.text.toString().trim()
    val currentDate = getCurrentDate()
    val currentTime = getCurrentTime()
    val uid = auth.uid
    val timestamp = System.currentTimeMillis()
    val hashMap : HashMap<String, Any?> = HashMap()

    hashMap["uid"] = uid
    hashMap["email"] = email
    hashMap["password"] = pass
    hashMap["fullName"] = fullname
    hashMap["image"] = imageUrl
    hashMap["currentDate"] = currentDate
    hashMap["currentTime"] = currentTime
    hashMap["id"] = "$timestamp"
    hashMap["userType"] = "member"
    hashMap["RFID"] = rfidData
    hashMap["PIN"] = pin
    hashMap["status"] = true

    try {
        database.getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(hashMap)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    findNavController().apply {
                        popBackStack(R.id.signUpFragment, false) // Pop all fragments up to HomeFragment
                        navigate(R.id.loginFragment) // Navigate to LoginFragment
                        database.getReference("Register").setValue("False")
                    }
                    Toast.makeText(this.requireContext(),"Account Created", Toast.LENGTH_SHORT).show()
                    database.getReference("RFID").setValue("")

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
        val formatter = SimpleDateFormat(   "dd-MM-yyyy")
        return formatter.format(currentDateObject)
    }
    private fun uploadInfo2() {

        this.rfidData = binding.etRfid.text.toString().trim()
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["$rfidData"] = rfidData

        try {
            database.getReference("RegRFID")
                .child(rfidData)
                .setValue(hashMap)
                .addOnCompleteListener { task ->

                }
        } catch (e: Exception) {
            // Handle any exceptions that might occur during the upload process.
            progressDialog.dismiss()
            Toast.makeText(
                this.requireContext(),
                "Error uploading data: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun uploadInfo3() {

        this.pin = binding.etPasscode.text.toString().trim()
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["$pin"] = pin

        try {
            database.getReference("RegPin")
                .child(pin)
                .setValue(hashMap)
                .addOnCompleteListener { task ->

                }
        } catch (e: Exception) {
            // Handle any exceptions that might occur during the upload process.
            progressDialog.dismiss()
            Toast.makeText(
                this.requireContext(),
                "Error uploading data: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
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
            database.getReference("Register").setValue("False")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}