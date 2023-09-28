package com.example.easylock.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.easylock.R
import com.example.easylock.databinding.FragmentUserPinragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserPinragment : Fragment() {
    private lateinit var binding : FragmentUserPinragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserPinragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        loadUsersInfo()
        binding.keypadButton0.setOnClickListener {
            binding.etRfid.append("0");
        }
        binding.keypadButton1.setOnClickListener {
            binding.etRfid.append("1");
        }
        binding.keypadButton2.setOnClickListener {
            binding.etRfid.append("2");
        }
        binding.keypadButton3.setOnClickListener {
            binding.etRfid.append("3");
        }
        binding.keypadButton4.setOnClickListener {
            binding.etRfid.append("4");
        }
        binding.keypadButto5.setOnClickListener {
            binding.etRfid.append("5");
        }
        binding.keypadButton6.setOnClickListener {
            binding.etRfid.append("6");
        }
        binding.keypadButton7.setOnClickListener {
            binding.etRfid.append("7");
        }
        binding.keypadButto8.setOnClickListener {
            binding.etRfid.append("8");
        }
        binding.keypadButton9.setOnClickListener {
            binding.etRfid.append("9")
        }

        binding.keypadButtonX.setOnClickListener {
            val currentText = binding.etRfid.text.toString()
            if (currentText.isNotEmpty()) {
                binding.etRfid.setText(currentText.substring(0, currentText.length - 1))
            }
        }

        binding.keypadButtonOK.setOnClickListener {
            getPinToUsers()
        }
    }
    private fun getPinToUsers() {
        val enteredPin = binding.etRfid.text.toString()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val storedPin = snapshot.child("PIN").getValue(String::class.java)

                    if (enteredPin == storedPin) {
                        // PINs match, save log
                        binding.etRfid.text?.clear()
                        database.getReference("Lock").setValue("Open")
                        Snackbar.make(binding.root, "Door is Open", Snackbar.LENGTH_SHORT).show()

                        // open door lock
                    } else {
                        // PINs don't match, handle accordingly
                        Toast.makeText(this@UserPinragment.requireContext(),"Pin Not Found Or Empty",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
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

                    //set data
                    binding.sampleHolder.text = rfid

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}