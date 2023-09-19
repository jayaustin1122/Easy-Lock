package com.example.easylock.user

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
import com.bumptech.glide.Glide
import com.example.easylock.R
import com.example.easylock.databinding.FragmentEditUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Date


class EditUserFragment : Fragment() {
    private lateinit var binding : FragmentEditUserBinding
    private lateinit var auth : FirebaseAuth
    var fullname = ""
    var pass = ""
    var image = ""
    var pin = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var selectedImage : Uri
    private lateinit var storage : FirebaseStorage
    private lateinit var database : FirebaseDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditUserBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUsersInfo()
        binding.btnUpdate.setOnClickListener{
            updateData()
        }
        binding.imageView.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)

        }
    }
    private fun updateData() {
        //get data
        fullname = binding.etName.text.toString()
        pass = binding.etPass.text.toString()
        pin = binding.pinCode.text.toString()

        if (fullname.isEmpty()) {
            Toast.makeText(
                this@EditUserFragment.requireContext(),
                "Empty fields are not allowed",
                Toast.LENGTH_SHORT
            ).show()
        } else if (pass.isEmpty()) {
            Toast.makeText(
                this@EditUserFragment.requireContext(),
                "Empty fields are not allowed",
                Toast.LENGTH_SHORT
            ).show()
        } else if (pin.isEmpty()) {
            Toast.makeText(
                this@EditUserFragment.requireContext(),
                "Empty fields are not allowed",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            uploadImage()
        }
    }
    private fun uploadImage() {
        progressDialog.setMessage("Uploading New Image...")
        progressDialog.show()

        val reference = storage.reference.child("profile")
            .child(Date().time.toString())
        reference.putFile(selectedImage).addOnCompleteListener{
            if (it.isSuccessful){
                reference.downloadUrl.addOnSuccessListener {task->
                    uploadInfo(task.toString())
                }
            }
        }

    }
    private fun uploadInfo(imgUrl: String) {
        progressDialog.setMessage("Updating Account")
        progressDialog.show()

        //set up to db
        val hashMap = HashMap<String, Any?>()

        hashMap["password"] = pass
        hashMap["fullName"] = fullname
        hashMap["PIN"] = pin
        hashMap["image"] = imgUrl

        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@EditUserFragment.requireContext(), "Account Updated", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editAccountsFragment_to_accountsFragment)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@EditUserFragment.requireContext(), "Unable to update due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

    }
    private fun loadUsersInfo() {
        //reference
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get user info
                    val rfid = "${snapshot.child("RFID").value}"
                    val currentDate = "${snapshot.child("currentDate").value}"
                    val currentTime = "${snapshot.child("currentTime").value}"
                    val email = "${snapshot.child("email").value}"
                    val fullName = "${snapshot.child("fullName").value}"
                    val id = "${snapshot.child("id").value}"
                    val image = "${snapshot.child("image").value}"
                    val password = "${snapshot.child("password").value}"
                    val pin = "${snapshot.child("PIN").value}"
                    val userType = "${snapshot.child("userType").value}"


                    binding.etName.setText(fullName)
                    binding.etPass.setText(password)
                    binding.pinCode.setText(pin)
                    binding.tvID.text = rfid
                    binding.tvEmail.text = email
                    Glide.with(requireActivity())
                        .load(image)
                        .into(binding.imageView)


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}