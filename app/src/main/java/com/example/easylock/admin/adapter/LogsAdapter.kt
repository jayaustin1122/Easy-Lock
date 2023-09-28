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
import com.example.easylock.databinding.AccountItemRowBinding
import com.example.easylock.databinding.LogsItemRowBinding
import com.example.easylock.model.AccountModel
import com.example.easylock.model.LogsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogsAdapter: RecyclerView.Adapter<LogsAdapter.ViewHolderLogs> {


    private lateinit var binding : LogsItemRowBinding
    private val context : Context
    var logsArrayList : ArrayList<LogsModel>


    constructor(context: Context, logsArrayList: ArrayList<LogsModel>){
        this.context = context
        this.logsArrayList = logsArrayList
    }

    inner class ViewHolderLogs(itemView: View): RecyclerView.ViewHolder(itemView){
        var rfid : TextView = binding.tvID
        var image : ImageView = binding.imgPicture
        var date : TextView = binding.tvDate
        var time : TextView = binding.textViewNoteTime

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsAdapter.ViewHolderLogs {
        //binding ui row item event
        binding = LogsItemRowBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolderLogs(binding.root)
    }
    override fun getItemCount(): Int {
        return logsArrayList.size
    }
    override fun onBindViewHolder(holder: LogsAdapter.ViewHolderLogs, position: Int) {
        //get data
        val model = logsArrayList[position]
        val time = model.time
        val date = model.date
        val rfid = model.RFID

        holder.date.text = date
        holder.time.text = time
        getUserData(rfid) { fullName ->
            holder.rfid.text = fullName
        }

    }
    private fun getUserData(rfid: String, onUserDataReceived: (String) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(rfid)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel = snapshot.getValue(AccountModel::class.java)
                userModel?.let {
                    onUserDataReceived(it.fullName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}