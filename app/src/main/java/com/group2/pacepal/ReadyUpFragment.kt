package com.group2.pacepal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.readyup_fragment.*
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView

import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot

//add the mile that is clicked on , or the default of 1 mile, to the database at the root of the sessionIndex as the end condition for that session
class ReadyUpFragment : Fragment() {


    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val rtdb = FirebaseDatabase.getInstance().reference
    var playercount = 0
    var players: MutableList<String> = ArrayList()
    var keyList: MutableList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        return inflater.inflate(R.layout.readyup_fragment, container, false)
        }

    //Add to the ReadyUpFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)               //gets SharedPreferences
        val sessionID = preferences.getString("sessionID", "")

        val spinner = Options_Spinner

        if(sessionID != userid){ //Hides the spinner from non-host users.
            spinner.visibility = View.INVISIBLE
        }
        else{

            spinner.adapter = ArrayAdapter(activity,
                    R.layout.support_simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.Miles_Array)
            )

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    Log.v("Item Value", "This: " + parent.getItemAtPosition(position))
                    rtdb.child("sessionManager")
                            .child("sessionIndex")
                            .child(userid)
                            .child("winCondition")
                            .setValue(parent.getItemAtPosition(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    /*Do something if nothing selected*/
                }
            }
        }




    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //super.onResume()
        super.onActivityCreated(savedInstanceState)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val sessionID = preferences.getString("sessionID", "")

        val adapter = ReadyUpRecyclerAdapter(players,this.context!!)
        var recyclerInit = false

        val playerlistener = object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                if((p0.key.toString() != "absoluteReady") && (p0.key.toString() != userid) && !players.contains(p0.key.toString())) {

                    players.add(p0.key.toString())
                }

                if(!recyclerInit && (players.size > 0)){
                    val invView = remotePlayersRecycler      //defines adapter for RecyclerView for invites
                    invView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    invView.adapter = adapter
                    recyclerInit = true

                }

                keyList.add(p0.key.toString())
                adapter.notifyDataSetChanged()

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
                //var index = keyList.indexOf(p0.key.toString())
                //players.removeAt(index)
                //adapter.notifyDataSetChanged()
            }
        }

        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").addChildEventListener(playerlistener)




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

                for(p in players){
                    if(!tempAbsolute)
                        break
                    tempAbsolute = dataSnapshot.child(p).value.toString().toBoolean()
                }

                if(tempAbsolute && (dataSnapshot.childrenCount > 2))
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID)
                            .child("ready").child("absoluteReady").setValue(tempAbsolute)
                //launch session

                if(dataSnapshot.child("sessionManager").child("sessionIndex").child(sessionID).exists()){
                    activity?.finish()
                }
                else{

                }

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

