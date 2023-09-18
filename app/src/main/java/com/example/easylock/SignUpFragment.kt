package com.example.easylock

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.easylock.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
            findNavController().apply {
                navigate(R.id.loginFragment) // Navigate to LoginFragment
            }
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
    private var rfid = ""
    private var pin = ""


    private fun validateData() {
        val email = binding.etEmailSignUp.text.toString().trim()
        val pass = binding.etPasswordSignUp.text.toString().trim()
        val fullname = binding.etFullname.text.toString().trim()
        val address = binding.etPasscode.text.toString().trim()

        // Validate user input
        if (email.isEmpty() || pass.isEmpty() || fullname.isEmpty() || address.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount(email, pass)
        }
    }
    private fun createUserAccount(email: String, pass: String) {
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email,pass)

            .addOnSuccessListener {
                // if user successfully created ()
                uploadImage()
            }
            .addOnFailureListener { e ->
                //if the user fialef creating account
                progressDialog.dismiss()
                Toast.makeText(this.requireContext(),"Failed Creating Account or ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }


    private fun uploadImage() {
        progressDialog.setMessage("Uploading Image...")
        progressDialog.show()
        val uid = auth.uid

        val reference = storage.reference.child("profile")
            .child(uid!!)
        reference.putFile(selectedImage).addOnCompleteListener{
            if (it.isSuccessful){
                reference.downloadUrl.addOnSuccessListener {task->
                    // Pass the RFID data to uploadInfo
                    uploadInfo(task.toString())
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(this@SignUpFragment.requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun uploadInfo(imageUrl: String) {
        progressDialog.setMessage("Saving Account...")
        progressDialog.show()
        email = binding.etEmailSignUp.text.toString().trim()
        pass = binding.etPasswordSignUp.text.toString().trim()
        fullname = binding.etFullname.text.toString().trim()
        rfid = binding.etRfid.text.toString().trim()
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
        hashMap["RFID"] = rfid
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
                        }
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