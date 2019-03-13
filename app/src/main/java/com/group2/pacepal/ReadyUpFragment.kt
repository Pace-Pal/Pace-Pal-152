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
                val allPlayers = p0.children
                allPlayers.forEach{
                    var temparray: MutableList<String> = ArrayList()
                    var tempcount = 0
                    if(it.value != user!!.uid) {
                        tempcount++
                        textViews += palStatus
                        temparray.add(it.key.toString())
                        Log.d("addingplayer",it.key.toString())
                    }
                    playercount = tempcount
                    players = temparray
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("rtdb error", "lost connection to database profile updates")
            }

            //android studio yelled at me if i didn't include these three functions
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }


        return inflater.inflate(R.layout.readyup_fragment, container, false)
        }




    override fun onResume() {
        super.onResume()

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



        val p2Listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var tempAbsolute = buttonState

                players.forEach{
                    if((dataSnapshot.child(it).value == false)){
                        tempAbsolute = false
                    }
                }

                if(tempAbsolute)
                    rtdb.child("absoluteReady").setValue(tempAbsolute)


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
        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").addValueEventListener(p2Listener)

    }



    companion object {
        fun newInstance(): ReadyUpFragment = ReadyUpFragment()
    }



}

