package com.example.easylock.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easylock.R
import com.example.easylock.admin.tab.LogsAdminFragment
import com.example.easylock.databinding.AccountItemRowBinding
import com.example.easylock.databinding.LogsItemRowBinding
import com.example.easylock.model.AccountModel
import com.example.easylock.model.LogsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogsAdapter(
    private val fragment: LogsAdminFragment,
    private val logsArrayList: ArrayList<LogsModel>
) : RecyclerView.Adapter<LogsAdapter.ViewHolderLogs>() {



    inner class ViewHolderLogs(itemView: View): RecyclerView.ViewHolder(itemView){
        var rfid: TextView = itemView.findViewById(R.id.tvID)
        var date: TextView = itemView.findViewById(R.id.tvDate)
        var time: TextView = itemView.findViewById(R.id.textViewNoteTime)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderLogs {
        val binding = LogsItemRowBinding.inflate(LayoutInflater.from(fragment.requireContext()), parent, false)
        return ViewHolderLogs(binding.root)
    }

    override fun getItemCount(): Int {
        return logsArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolderLogs, position: Int) {
        val model = logsArrayList[position]
        val time = model.time
        val date = model.date
        val rfid = model.RFID

        holder.date.text = date
        holder.time.text = time
        retrieveUserName(rfid, holder.rfid)
    }

    private fun retrieveUserName(rfid: String, textView: TextView) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.orderByChild("RFID").equalTo(rfid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val userModel = data.getValue(AccountModel::class.java)
                    val userName = userModel?.fullName

                    // Display user name
                    textView.text = userName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


}