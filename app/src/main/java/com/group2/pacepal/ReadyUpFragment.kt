package com.group2.pacepal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.readyup_fragment.*
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.session_activity.*


class ReadyUpFragment : Fragment() {



    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    //private val sessionID = arguments!!.getString("sessionID")
    private val rtdb = FirebaseDatabase.getInstance().reference

    //data class ReadyState(var p1Ready: String? = "",)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {





        return inflater.inflate(R.layout.readyup_fragment, container, false)
        }


    override fun onResume() {
        super.onResume()

        Log.d("readyUP", "resumed")
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val sessionID = preferences.getString("sessionID", "")

        val palStatus = view!!.findViewById<TextView>(R.id.palStatus)

        var p1Ready = false
        var p2Ready = false
        var absReady = false

        var buttonState = false


        val readyClicker = readyButton

        if (preferences.getBoolean("initState", false)) {
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

                if (sessionID == userid)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("p1Ready")
                            .setValue(buttonState)
                else
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("p2Ready")
                            .setValue(buttonState)


            }
        }





        val hostListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                p2Ready = dataSnapshot.child("ready").child("p2Ready").value.toString().toBoolean()

                if(p2Ready)
                    palStatus.text = "Ready"
                else
                    palStatus.text = "Not Ready"

                if(p2Ready && buttonState) {
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("absoluteReady")
                            .setValue(true)
                    val editor = preferences.edit()
                    editor.putBoolean("readyState", true)
                    editor.commit()

                    fragmentManager?.popBackStack()



                }




            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }

        val p2Listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                p1Ready = dataSnapshot.child("ready").child("p1Ready").value.toString().toBoolean()

                if(p1Ready)
                    palStatus.text = "Ready"
                else
                    palStatus.text = "Not Ready"



                if(p1Ready && buttonState) {
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("absoluteReady")
                            .setValue(true)
                    val editor = preferences.edit()
                    editor.putBoolean("readyState", true)
                    editor.commit()
                    fragmentManager?.popBackStack()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }

        //Log.d("init readyUp session", sessionID)
        //Toast.makeText(context,sessionID,Toast.LENGTH_SHORT)
        if(userid == sessionID)
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).addValueEventListener(hostListener)
        else {
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).addValueEventListener(p2Listener)
        }
    }

    companion object {
        fun newInstance(): ReadyUpFragment = ReadyUpFragment()
    }



}