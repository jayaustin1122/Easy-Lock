package com.example.easylock.admin.tab

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easylock.R
import com.example.easylock.admin.adapter.AccountsAdapter
import com.example.easylock.databinding.FragmentAccountsBinding
import com.example.easylock.model.AccountModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AccountsFragment : Fragment() {

    private lateinit var binding : FragmentAccountsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    // array list to hold events
    private lateinit var accArrayList : ArrayList<AccountModel>

    //adapter
    private lateinit var adapter : AccountsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this@AccountsFragment.requireContext())
        progressDialog.setTitle("PLease wait")
        progressDialog.setCanceledOnTouchOutside(false)
        getAccounts()

    }
    private fun getAccounts() {
        //initialize
        accArrayList = ArrayList()

        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear list
                accArrayList.clear()
                for (data in snapshot.children){
                    //data as model
                    val model = data.getValue(AccountModel::class.java)

                    // Check if the userType is "member"
                    if (model?.userType == "member") {
                        // add to array
                        accArrayList.add(model!!)
                    }
                }
                //set up adapter
                adapter = AccountsAdapter(this@AccountsFragment.requireContext(), accArrayList)
                //set to recycler
                binding.adminEventRv.setHasFixedSize(true)
                binding.adminEventRv.layoutManager = LinearLayoutManager(context)
                binding.adminEventRv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}