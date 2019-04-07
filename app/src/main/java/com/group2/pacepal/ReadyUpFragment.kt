package com.group2.pacepal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.readyup_fragment.*
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.session_activity.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.database.DataSnapshot




class ReadyUpFragment : Fragment() {


    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val rtdb = FirebaseDatabase.getInstance().reference
    var textViews = emptyList<TextView>()
    var playercount = 0
    var players: MutableList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //listens for state changes of other players
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val sessionID = preferences.getString("sessionID", "")

        val playerlistener = object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var tempTracker = false

                for(x in players)
                    if(x == p0.key)
                        tempTracker = true

                if(!tempTracker){
                    if((p0.key != user!!.uid)&&(p0.key != "absoluteReady")){
                        var temparray: MutableList<String> = ArrayList()
                        var tempcount = 0
                        tempcount++
                        temparray.add(p0.key.toString())
                        Log.d("addingplayer",p0.key.toString())
                        playercount = tempcount
                        players = temparray
                    }
                }

            }



            override fun onCancelled(p0: DatabaseError) {
                Log.d("rtdb error", "lost connection to database profile updates")
            }

            //android studio yelled at me if i didn't include these three functions
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                //nothing
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                //nothing
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                //nothing
            }
        }

        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").addChildEventListener(playerlistener)


        return inflater.inflate(R.layout.readyup_fragment, container, false)
        }


    override fun onActivityCreated(savedInstanceState: Bundle?)

 {
        //super.onResume()
     super.onActivityCreated(savedInstanceState)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val sessionID = preferences.getString("sessionID", "")
        Log.d("readyUP", "resumed")

        val palStatus = view!!.findViewById<TextView>(R.id.palStatus)

        var absReady = false

        var buttonState = false


        val readyClicker = readyButton

        readyClicker.setOnClickListener {
                buttonState = !buttonState

                if (buttonState){
                    readyClicker.text = "Unready"
                    yourStatus.text = "Ready!"
                }
                else{
                    readyClicker.text = "Ready"
                    yourStatus.text = "Not Ready"
                }

                rtdb.child("sessionManager").child("sessionIndex").child(sessionID)
                        .child("ready").child(user!!.uid)
                            .setValue(buttonState)

            }



        val readyListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var tempAbsolute = buttonState
                Log.d("DATABASE_CHANGE", "changed")
                players.forEach{
                    if((dataSnapshot.child(it).value == false)){
                        tempAbsolute = false
                    }
                }

                if(tempAbsolute)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID)
                            .child("ready").child("absoluteReady").setValue(tempAbsolute)
                    //launch session

                if(playercount == 0)
                    Log.d("playercount",":0")
                else if(dataSnapshot.child(players[0]).value == true)
                    palStatus.text = "Ready"
                else
                    palStatus.text = "Not Ready"

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }

        //Log.d("init readyUp session", sessionID)
        //Toast.makeText(context,sessionID,Toast.LENGTH_SHORT)
        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").addValueEventListener(readyListener)

    }

    override fun onPause() {
        super.onPause()
    }



    companion object {
        fun newInstance(): ReadyUpFragment = ReadyUpFragment()
    }



}

