package com.group2.pacepal

import android.graphics.Color
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.geometry.LatLng


//listens for location changes for a specific player
data class RemotePlayer(val userID:String, val sessionID:String) {

    val rtdb = FirebaseDatabase.getInstance().reference
    val fsdb = FirebaseFirestore.getInstance()

    private var distance = 0.0
    private var long = 0.0
    private var lat = 0.0
    private var remotePolyline = PolylineOptions()

    private var pictureURL = "https://firebasestorage.googleapis.com/v0/b/pace-pal-ad8c4.appspot.com/o/defaultAVI.png?alt=media&token=6c9c47df-8151-4e5b-8843-3440e317346c"
    private var username = ""

    //attaches a listener for the passed in player
    init{
    attachListener()
    getFirestoreInfo()
    }

    private fun attachListener(){

        remotePolyline.color(Color.RED)
        remotePolyline.width(3F)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("myMap", dataSnapshot.toString())
                distance = dataSnapshot.child("distance").value.toString().toDouble()
                long = dataSnapshot.child("long").value.toString().toDouble()
                lat = dataSnapshot.child("lat").value.toString().toDouble()
                remotePolyline.add(LatLng(lat,long))
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

    private fun getFirestoreInfo(){
        val docRef = fsdb.collection("users").document(userID)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentProfile = task.result
                pictureURL = currentProfile!!.getString("profilepic").toString()
                username = currentProfile!!.getString("username").toString()
            } else {
                Log.d("RemotePlayer ","Unable to contact FireStore")
            }

        }
    }

    fun getUID(): String {return userID}
    fun getDistance():Double {return this.distance }
    fun getLong():Double {return this.long }
    fun getLat():Double {return this.lat }
    fun getID():String { return this.userID }
    fun getUsername():String {return this.username}
    fun getPic():String {return this.pictureURL}
    fun getPolyline(): PolylineOptions {return this.remotePolyline}
    //fun setPolyline(newPline:PolylineOptions) {this.remotePolyline = newPline}
}