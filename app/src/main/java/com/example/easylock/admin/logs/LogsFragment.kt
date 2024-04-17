package com.example.easylock.admin.logs

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easylock.R
import com.example.easylock.admin.adapter.LogsAdapter
import com.example.easylock.databinding.FragmentLogsBinding
import com.example.easylock.model.LogsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale


class LogsFragment : Fragment() {
    private lateinit var binding : FragmentLogsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    // array list to hold events
    private lateinit var logsArrayList : ArrayList<LogsModel>
    //adapter
    private lateinit var adapter : LogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this@LogsFragment.requireContext())
        progressDialog.setTitle("PLease wait")
        progressDialog.setCanceledOnTouchOutside(false)
        // Initialize userMap here before calling getLogs()
        getLogs()
        //search
        binding.etSearch2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterLogs(s.toString().toLowerCase(Locale.ROOT))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }


    private fun getLogs() {
        //initialize
        logsArrayList = ArrayList()

        val dbRef = FirebaseDatabase.getInstance().getReference("Logs")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear list
                logsArrayList.clear()
                for (data in snapshot.children){
                    //data as model
                    val model = data.getValue(LogsModel::class.java)

                    // add to array
                    logsArrayList.add(model!!)
                }

                //set up adapter
                adapter = LogsAdapter(this@LogsFragment, logsArrayList)
                binding.recycler.setHasFixedSize(true)
                binding.recycler.layoutManager = LinearLayoutManager(context,).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                binding.recycler.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }




    private fun filterLogs(query: String) {
        val filteredList = ArrayList<LogsModel>()
        for (log in logsArrayList) {
            if (log.RFID.toLowerCase(Locale.ROOT).contains(query)) {
                filteredList.add(log)
            }
        }
        adapter.updateList(filteredList)
    }


}