package com.group2.pacepal                //The main fragment in the home menu, for starting and joining sessions

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.se.omapi.Session
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
//import com.google.firebase.database.ValueEventListener


class SessionFragment : Fragment() {

    private val fsdb = FirebaseFirestore.getInstance()             //firestore database for profiles
    private val user = FirebaseAuth.getInstance().currentUser        //auth information for signed in user
    private val userid = user!!.uid
    private val invitesList = ArrayList<Invite>(0)                   //holds invites
    private val adapter = RecyclerAdapter1(invitesList)                           //adapter for RecyclerView for invites
    private val rtdb = FirebaseDatabase.getInstance().reference  //realtiem database to look for invites


    private lateinit var inviteRefrence: DatabaseReference


    private lateinit var linearLayoutManager:LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.session_menu, container, false)       //inflates layout
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)               //gets SharedPreferences

        val invView = view?.findViewById(R.id.sessionInvites) as RecyclerView      //defines adapter for RecyclerView for invites
        invView.layoutManager = LinearLayoutManager(this.context)
        invView.adapter = adapter

        val refreshButton = view.findViewById<Button>(R.id.inviteRefresh)    //sets listener for invite refresh
        refreshButton.setOnClickListener { refreshInvites() }

        val socialButton = view.findViewById<Button>(R.id.socialCreate)      //sets listener for creating Social session
        socialButton.setOnClickListener{
            val editor = preferences.edit()
            editor.clear()
            editor.commit()
            //editor.putBoolean("readyState", false)
            //editor.putBoolean("initState", false)
            editor.putString("sessionID", userid)
            editor.putString("sessionType","3")
            //editor.putBoolean("friendInvited", false)
            editor.commit()
            val parentContext = context
            val intent = Intent(parentContext, SessionActivity::class.java)
            parentContext!!.startActivity(intent)


        }

        refreshInvites()                        //initial refresh for invites




        return view
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
        invitesList.clear()
        //inviteRefresh.text = "loading.."
        val intentContext = this.context!!
        val friendsList = fsdb.collection("users").document(userid).collection("friends")
        friendsList.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.size() == 0)
                            Toast.makeText(context, "No friends found.", Toast.LENGTH_SHORT).show()
                        for (document in task.result!!) {
                            //invitesList.add(Invite(document.id, document.id, document.id))
                            inviteRefrence = FirebaseDatabase.getInstance().reference

                            val invListener = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if(dataSnapshot.child("P2").value.toString() == userid) {

                                        val host = fsdb.collection("users").document(document.id)
                                        host.get().addOnSuccessListener { hostProfile ->
                                            val hostUsername = hostProfile.getString("username").toString()
                                            val hostUID = document.id
                                            var sessionType = dataSnapshot.child("type").value.toString()
                                            if(sessionType != "0") {
                                                if(sessionType == "1") sessionType = "Competitive"
                                                if(sessionType == "2") sessionType = "Collaborative"
                                                if(sessionType == "3") sessionType = "Social"

                                                invitesList.add(Invite(hostUsername, hostUID, sessionType,intentContext))
                                                adapter.notifyDataSetChanged()
                                            }
                                    }

                                }}

                                override fun onCancelled(databaseError: DatabaseError) {
                                    println("loadPost:onCancelled ${databaseError.toException()}")
                                }
                           }

                            rtdb.child("sessionManager").child("sessionIndex").child(document.id).addListenerForSingleValueEvent(invListener)

                            }


                        adapter.notifyDataSetChanged()

                    }
                    else
                        Toast.makeText(context,"Connection error.", Toast.LENGTH_SHORT)
                }



    }


}