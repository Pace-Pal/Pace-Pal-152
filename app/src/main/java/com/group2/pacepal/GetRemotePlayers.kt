package com.group2.pacepal

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//pass in sessionID, get back ArrayList of players in session

data class GetRemotePlayers(val sessionID:String){

    private var playerClassList: MutableList<RemotePlayer> = ArrayList()
    private var remotePlayerCount = 0

    init{generatePlayers()}

    private fun generatePlayers(){
        val rtdb = FirebaseDatabase.getInstance().reference
        var userid = FirebaseAuth.getInstance().currentUser!!.uid //gets firebase info for current user and databases

        val playersGet = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("myMap",p0.toString())
                remotePlayerCount = p0.childrenCount.toString().toInt() - 1
                p0.children.forEach{
                    if(it.key != userid) {
                        //players.add(it.key.toString())
                        //rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").child(it.key.toString()).addValueEventListener(postListener)
                        playerClassList.add(RemotePlayer(it.key.toString(),sessionID))
                        //polylines.add(com.mapbox.mapboxsdk.annotations.PolylineOptions())
                    }
                }
            }
        }
        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").addListenerForSingleValueEvent(playersGet)
    }

    fun getPlayerList():MutableList<RemotePlayer> {return this.playerClassList }
    fun getPlayerCount():Int {return this.remotePlayerCount}

}