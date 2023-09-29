package com.example.easylock.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easylock.R
import com.example.easylock.admin.adapter.AccountsAdapter
import com.example.easylock.admin.tab.LogsAdminFragment
import com.example.easylock.databinding.AccountItemRowBinding
import com.example.easylock.databinding.LogsItemRowBinding
import com.example.easylock.model.AccountModel
import com.example.easylock.model.LogsModel

class MyLogsAdapter(
    private val fragment: UserFragment,
    private val logsArrayList: ArrayList<LogsModel>
): RecyclerView.Adapter<MyLogsAdapter.ViewHolderMyLogs>() {


    inner class ViewHolderMyLogs(itemView: View): RecyclerView.ViewHolder(itemView){
        var rfid: TextView = itemView.findViewById(R.id.tvID)
        var date: TextView = itemView.findViewById(R.id.tvDate)
        var time: TextView = itemView.findViewById(R.id.textViewNoteTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMyLogs {
        val binding = LogsItemRowBinding.inflate(
            LayoutInflater.from(fragment.requireContext()),
            parent,
            false
        )
        return ViewHolderMyLogs(binding.root)
    }

    override fun getItemCount(): Int {
        return logsArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolderMyLogs, position: Int) {
        val model = logsArrayList[position]
        val time = model.time
        val date = model.date
        val rfid = model.RFID

        holder.date.text = date
        holder.time.text = time
        holder.rfid.text = generateRandomNumber()
    }
    private fun generateRandomNumber(): String {
        val random = java.util.Random()
        val randomNum = random.nextInt(1000000000) + 1000000000 // Ensure 10 digits
        return randomNum.toString()
    }

}