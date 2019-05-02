package com.group2.pacepal                //The main fragment in the home menu, for starting and joining sessions

import android.app.ActionBar
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.se.omapi.Session
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import com.google.api.Distribution
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.session_menu.*
//import mypackage.util.ContextExtensions.toast
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.*
import android.widget.LinearLayout
import android.widget.TextView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.*
import java.util.*

//import com.google.firebase.database.ValueEventListener


class SessionFragment : Fragment() {

    private val fsdb = FirebaseFirestore.getInstance()             //firestore database for profiles
    private val user = FirebaseAuth.getInstance().currentUser        //auth information for signed in user
    private val userid = user!!.uid
    private val invitesList = ArrayList<Invite>(0)                   //holds invites
    private val adapter = RecyclerAdapter1(invitesList)                           //adapter for RecyclerView for invites
    private val rtdb = FirebaseDatabase.getInstance().reference  //realtiem database to look for invites
    private val TTSHolder = ArrayList<TextToSpeech>(0)

    var ttsVal = "false"

    private lateinit var inviteRefrence: DatabaseReference


    private lateinit var linearLayoutManager:LinearLayoutManager

    /*
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater){
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)

        //menu.findItem(R.id.searchNewFriends).setVisible(false)
    }
*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.session_menu, container, false)       //inflates layout

        val invView = view?.findViewById(R.id.sessionInvites) as RecyclerView      //defines adapter for RecyclerView for invites
        invView.layoutManager = LinearLayoutManager(this.context)
        invView.adapter = adapter

        //**********Need to pull the value of the sharedpreference that states if tts is on or off. Default is off

        var prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        ttsVal = prefs.getString("TTSValue", "potato" )


        //Reads off values from the strings passed in
        val A = TextToSpeech(this.context)
        TTSHolder.add(A)



        val refreshButton = view.findViewById<Button>(R.id.inviteRefresh)    //sets listener for invite refresh
        refreshButton.setOnClickListener { refreshInvites() }

        val socialButton = view.findViewById<Button>(R.id.socialCreate)      //sets listener for creating Social session
        socialButton.setOnClickListener{
            createSession(3)
        }

        val compButton = view.findViewById<Button>(R.id.compCreate)      //sets listener for creating competitive session
        compButton.setOnClickListener{
            createSession(1)
        }

        val colabButton = view.findViewById<Button>(R.id.colabCreate)      //sets listener for creating colab session
        colabButton.setOnClickListener{
            createSession(2)
        }

        refreshInvites()                        //initial refresh for invites



        return view
    }


    private fun createSession(sesType:Int) {  //create session
        //init info for leader location
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("players").child(user!!.uid).child("long").setValue(0.0)
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("players").child(user!!.uid).child("lat").setValue(0.0)
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("players").child(user!!.uid).child("distance").setValue(0.0)
        //init info for leader ready up
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("ready").child("absoluteReady").setValue(false)
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("ready").child(user!!.uid).setValue(false)
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("sessionEnded").setValue(false)
        //init general session info
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("sessionComplete").setValue(false)
        rtdb.child("sessionManager").child("sessionIndex").child(user!!.uid).child("type").setValue(sesType)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)               //gets SharedPreferences
        val editor = preferences.edit()
        editor.putString("sessionID", userid)
        editor.putString("sessionType", sesType.toString())
        editor.commit()

        //launches session activity
        val parentContext = context
        val intent = Intent(parentContext, SessionActivity::class.java)
        parentContext!!.startActivity(intent)



    }
    companion object {
        fun newInstance(): SessionFragment = SessionFragment()
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.inviteRefresh -> {
                refreshInvites()
                Toast.makeText(context,"Clicked!",Toast.LENGTH_SHORT).show()
            }
            //R.id.textview2-> {
            //    Toast.makeText(this, "Clicked 2", Toast.LENGTH_SHORT).show()
            //}
        }
    }


    private fun refreshInvites() {
        invitesList.clear()                              //clears any previously loaded entries
        val intentContext = this.context!!               //i still dont know how intent works
        val friendsList = fsdb.collection("users").document(userid).collection("friends")       //sets Firestore location for users friends list
        friendsList.get()
                .addOnCompleteListener { task ->                                        //gets friends list from Firestore
                    if (task.isSuccessful) {
                        if (task.result!!.size() == 0)
                            Toast.makeText(context, "No friends found.", Toast.LENGTH_SHORT).show()
                            if(ttsVal == "true") {
                                TTSHolder[0].speak("No friends found")
                            }
                        for (document in task.result!!) {
                            inviteRefrence = FirebaseDatabase.getInstance().reference                       //sets gets rtDatabase for current sessions

                            val invListener = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {


                                    if(dataSnapshot.child("invite").hasChild(userid)){                       //checks for any friend sessions where user is an invites player

                                        val host = fsdb.collection("users").document(document.id)
                                        host.get().addOnSuccessListener { hostProfile ->
                                            val hostUsername = hostProfile.getString("username").toString()
                                            val hostUID = document.id
                                            var sessionType = dataSnapshot.child("type").value.toString()
                                            if(sessionType != "0") {
                                                if(sessionType == "1") sessionType = "Competitive"                          //sets text for location type
                                                if(sessionType == "2") sessionType = "Collaborative"
                                                if(sessionType == "3") sessionType = "Social"

                                                invitesList.add(Invite(hostUsername, hostUID, sessionType,intentContext))
                                                adapter.notifyDataSetChanged()
                                                if(ttsVal == "true") {
                                                    TTSHolder[0].speak("Invite: from" + hostUsername + "Mode: " + sessionType)
                                                }
                                            }
                                        }

                                    }}

                                override fun onCancelled(databaseError: DatabaseError) {
                                    println("loadPost:onCancelled ${databaseError.toException()}")
                                }
                            }

                            rtdb.child("sessionManager").child("sessionIndex").child(document.id).addListenerForSingleValueEvent(invListener)

                        }


                        adapter.notifyDataSetChanged()                         //notifies recycler of change and reloads

                    }
                    else
                        Toast.makeText(context,"Connection error.", Toast.LENGTH_SHORT)
                }



    }

    override fun onPause() {
        super.onPause()
        TTSHolder[0].pause()
    }




}