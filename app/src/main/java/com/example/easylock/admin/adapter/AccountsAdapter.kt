package com.example.easylock.admin.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easylock.R
import com.example.easylock.admin.tab.accounts.EditAccountsFragment
import com.example.easylock.databinding.AccountItemRowBinding
import com.example.easylock.model.AccountModel
import com.google.firebase.database.FirebaseDatabase

class AccountsAdapter: RecyclerView.Adapter<AccountsAdapter.ViewHolderAccounts> {

    private lateinit var binding : AccountItemRowBinding
    private val context : Context
    var accountsArrayList : ArrayList<AccountModel>

    constructor(context: Context, accountsArrayList: ArrayList<AccountModel>){
        this.context = context
        this.accountsArrayList = accountsArrayList
    }

    inner class ViewHolderAccounts(itemView: View): RecyclerView.ViewHolder(itemView){
        var moreBtn : ImageButton = binding.btnMore
        var image : ImageView = binding.imgPicture
        var date : TextView = binding.tvDate
        var time : TextView = binding.textViewNoteTime
        var id : TextView = binding.tvID
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAccounts {
        //binding ui row item event
        binding = AccountItemRowBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolderAccounts(binding.root)
    }
    override fun getItemCount(): Int {
        return accountsArrayList.size
    }
    override fun onBindViewHolder(holder: ViewHolderAccounts, position: Int) {
        //get data
        val model = accountsArrayList[position]
        val fullname = model.fullName
        val id = model.RFID
        val time = model.currentTime
        val date = model.currentDate
        val imageselected = model.image

        holder.id.text = fullname
        holder.date.text = date
        holder.time.text = time
        Glide.with(this@AccountsAdapter.context)
            .load(imageselected)
            .into(holder.image)

        holder.moreBtn.setOnClickListener {
            moreOptions(model,holder)
        }

    }
    private fun moreOptions(model: AccountModel, holder: AccountsAdapter.ViewHolderAccounts) {
        //get id title
        val accId = model.id
        val fullname = model.fullName
        val pass = model.password
        val image = model.image
        val email = model.email
        val pin = model.PIN
        // show options
        val options = arrayOf("Edit","Delete","Block")
        // show alert dialog
        val  builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options){dialog,position ->
                //handle item clicked
                if (position == 0 ){
                    val bundle = Bundle()
                    bundle.putString("id", accId)
                    bundle.putString("fullname", fullname)
                    bundle.putString("pass", pass)
                    bundle.putString("image", image)
                    bundle.putString("email", email)
                    bundle.putString("pin", pin)

                    val editFragment = EditAccountsFragment()
                    editFragment.arguments = bundle

                    // Now, replace the current fragment with the EditAccountFragment
                    (context as AppCompatActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, editFragment) // Replace R.id.fragment_container with your actual container ID
                        .addToBackStack(null)
                        .commit()

                }
                else if (position == 1){
                    //delete btn
                    //dialog
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this Account?")
                        .setPositiveButton("Confirm"){a,d->
                            Toast.makeText(context,"Account Deleted", Toast.LENGTH_SHORT).show()

                            deleteEvent(model,holder)
                        }
                        .setNegativeButton("Cancel"){a,d->
                            a.dismiss()
                        }
                        .show()
                }
            }
            .show()

    }

    private fun deleteEvent(model: AccountModel, holder: AccountsAdapter.ViewHolderAccounts) {
        //id as the reference to delete

        val id = model.uid

        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.child(id.toString())
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,"Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context,"Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}