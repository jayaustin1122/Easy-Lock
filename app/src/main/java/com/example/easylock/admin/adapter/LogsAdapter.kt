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

class LogsAdapter: RecyclerView.Adapter<LogsAdapter.ViewHolderLogs> {


    private lateinit var binding : LogsItemRowBinding
    private val context : Context
    var logsArrayList : ArrayList<LogsModel>

    constructor(context: Context, logsArrayList: ArrayList<LogsModel>){
        this.context = context
        this.logsArrayList = logsArrayList
    }

    inner class ViewHolderLogs(itemView: View): RecyclerView.ViewHolder(itemView){
        var name : TextView = binding.tvID
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
        val fullname = model.fullName
        val time = model.currentTime
        val date = model.currentDate
        val imageselected = model.image

        holder.name.text = fullname
        holder.date.text = date
        holder.time.text = time
        Glide.with(this@LogsAdapter.context)
            .load(imageselected)
            .into(holder.image)



    }

}