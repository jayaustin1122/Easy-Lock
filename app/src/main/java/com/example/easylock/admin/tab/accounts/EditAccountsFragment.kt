package com.example.easylock.admin.tab.accounts

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.easylock.FragmentUtils
import com.example.easylock.R
import com.example.easylock.databinding.FragmentEditAccountsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date


class EditAccountsFragment : Fragment() {

    private lateinit var binding : FragmentEditAccountsBinding
    var rfid = ""
    var fullname = ""
    var pass = ""
    var image = ""
    var email = ""
    var pin = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var selectedImage : Uri
    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var database : FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditAccountsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(this@EditAccountsFragment.requireContext())
        progressDialog.setTitle("PLease wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //get id to edit events
        rfid = arguments?.getString("id").toString()
        fullname = arguments?.getString("fullname").toString()
        pass = arguments?.getString("pass").toString()
        image = arguments?.getString("image").toString()
        email = arguments?.getString("email").toString()
        pin = arguments?.getString("pin").toString()

        Glide.with(this)
            .load(image)
            .into(binding.imageView)

        binding.etName.setText(fullname)
        binding.etPass.setText(pass)
        binding.pinCode.setText(pin)
        binding.tvID.setText(rfid)
        binding.tvEmail.setText(email)
        binding.btnUpdate.setOnClickListener {
            updateData()

        }
        binding.imageView.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)

        }
        binding.btnBack.setOnClickListener {
            database.getReference("Register").setValue("False")
            findNavController().apply {
                navigate(R.id.accountsFragment) // Navigate to LoginFragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().apply {
                    popBackStack(R.id.editAccountsFragment, false) // Pop all fragments up to HomeFragment
                    navigate(R.id.accountsFragment) // Navigate to LoginFragment
                }
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if (data.data != null){
                selectedImage = data.data!!
                binding.imageView.setImageURI(selectedImage)
            }
        }
    }
    private fun updateData() {
        //get data
        fullname = binding.etName.text.toString()
        pass = binding.etPass.text.toString()
        pin = binding.pinCode.text.toString()

        if (fullname.isEmpty()) {
            Toast.makeText(this@EditAccountsFragment.requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
        else if (pass.isEmpty()) {
            Toast.makeText(this@EditAccountsFragment.requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
        else if (pin.isEmpty()) {
            Toast.makeText(this@EditAccountsFragment.requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
        else{
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
                Toast.makeText(this@EditAccountsFragment.requireContext(), "Account Updated", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editAccountsFragment_to_accountsFragment)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@EditAccountsFragment.requireContext(), "Unable to update due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

    }

}