package com.group2.pacepal

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


//listens for location changes for a specific player
data class RemotePlayer(val userID:String, val sessionID:String) {

    val rtdb = FirebaseDatabase.getInstance().reference

    private var distance = 0.0
    private var long = 0.0
    private var lat = 0.0
    val remotePolyline = com.mapbox.mapboxsdk.annotations.PolylineOptions()

    //attaches a listener for the passed in player
    init{attachListener()}


    private fun attachListener(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("myMap", dataSnapshot.toString())
                distance = dataSnapshot.child("distance").value as Double
                long = dataSnapshot.child("long").value as Double
                lat = dataSnapshot.child("lat").value as Double
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        }

        rtdb.child("sessionManager").child("sessionIndex")
                .child(this.sessionID).child("players").child(this.userID)
                .addValueEventListener(postListener)
    }

    fun getDistance():Double {return this.distance }
    fun getLong():Double {return this.long }
    fun getLat():Double {return this.lat }
    fun getID():String { return this.userID }
}