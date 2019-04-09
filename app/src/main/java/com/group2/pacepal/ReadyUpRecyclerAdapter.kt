package com.group2.pacepal

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater

import android.content.Context
import android.preference.PreferenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.remote_player_row_item.view.*
import kotlinx.android.synthetic.main.user_profile.view.*


class ReadyUpRecyclerAdapter (private val readys: MutableList<String>, private val parentContext:Context)  : RecyclerView.Adapter<ReadyUpRecyclerAdapter.ReadyHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadyHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.remote_player_row_item,parent,false)
        return ReadyHolder(inflatedView,parentContext)
    }

    override fun getItemCount() = readys.size

    override fun onBindViewHolder(holder: ReadyUpRecyclerAdapter.ReadyHolder, position: Int) {
        val itemReady = readys[position]
        //readys[position]

        holder.bindInvite(itemReady)
    }

    class ReadyHolder(v: View,c:Context) : RecyclerView.ViewHolder(v) , View.OnClickListener {

        private var view : View = v
        private var ready : String? = null
        private var parentContext = c

        init {
            v.setOnClickListener {this}
        }

        override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK!")

        }

        fun bindInvite(ready: String) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(parentContext)
            val sessionID = sharedPref.getString("sessionID", "")
            val fsdb = FirebaseFirestore.getInstance()
            val rtdb = FirebaseDatabase.getInstance().reference

            val docRef = fsdb.collection("users").document(ready)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentProfile = task.result
                    Log.d("ReacyUpRe",currentProfile.toString())
                    Log.d("ReacyUpRe",currentProfile!!.getString("username").toString())
                    Picasso.with(parentContext).load(currentProfile!!.getString("profilepic").toString()).fit().into(view.playerPicture)
                    view.playerName.text = currentProfile!!.getString("username").toString()

                } else {
                    Log.d("RemotePlayer ", "Unable to contact FireStore")
                }

            }

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value.toString().toBoolean())
                        view.playerStatus.text = "Ready"
                    else
                        view.playerStatus.text = "Not Ready"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            }
            rtdb.child("sessionManager").child("sessionIndex")
                    .child(sessionID).child("ready").child(ready)
                    .addValueEventListener(postListener)
        }
    }
}