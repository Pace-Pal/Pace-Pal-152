package com.group2.pacepal

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.remote_player_row_item.view.*

import android.support.v4.content.ContextCompat.startActivity
import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mapbox.mapboxsdk.geometry.LatLng
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.coroutines.coroutineContext
import kotlin.math.round


class RemotePlayerRecyclerAdapter (private val players: MutableList<RemotePlayer>,private val listenerOption:String,private val actContext: Context)  : RecyclerView.Adapter<RemotePlayerRecyclerAdapter.PlayerHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.remote_player_row_item,null,false)
        return PlayerHolder(inflatedView,listenerOption,actContext)
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: RemotePlayerRecyclerAdapter.PlayerHolder, position: Int) {
        val itemPlayer = players[position]

        holder.bindPlayer(itemPlayer)
    }

    class PlayerHolder(v: View,s:String,c:Context) : RecyclerView.ViewHolder(v) , View.OnClickListener {

        private var view : View = v
        private var player : String? = null
        private var listenerOption: String = s
        private var parentContext = c
        val rtdb = FirebaseDatabase.getInstance().reference

        init {
            v.setOnClickListener {this}
        }

        override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK!")

        }

        fun bindPlayer(player: RemotePlayer) {

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(parentContext)
            val sessionID = sharedPref.getString("sessionID", "")

            Log.d("remoteRecycler",player.getUID())

            view.playerName.text = player.getUsername()
            Picasso.with(parentContext).load(player.getPic()).fit().into(view.playerPicture)


            if(listenerOption == "players") {
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        view.playerStatus.text = round((dataSnapshot.value.toString().toDouble()),2).toString() + " mi"
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                }
                rtdb.child("sessionManager").child("sessionIndex")
                        .child(sessionID).child(listenerOption).child(player.getUID()).child("distance")
                        .addValueEventListener(postListener)
            }
            else if(listenerOption == "ready") {
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
                        .child(sessionID).child(listenerOption).child(player.getUID())
                        .addValueEventListener(postListener)
            }





        }

        fun round(value: Double, places: Int): Double {
            if (places < 0) throw IllegalArgumentException()

            var bd = BigDecimal(value)
            bd = bd.setScale(places, RoundingMode.HALF_UP)
            return bd.toDouble()
        }
    }
}