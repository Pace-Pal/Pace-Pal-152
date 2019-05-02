package com.group2.pacepal

import android.Manifest
import android.R.attr.data
import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.session_activity.*
import android.R.attr.fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class SessionActivity : AppCompatActivity() {

    //val sessionId = intent.getStringExtra("sessionID")
    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val fsdb = FirebaseFirestore.getInstance()
    private val rtdb = FirebaseDatabase.getInstance().reference
    private val players = arrayOfNulls<String>(4)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.session_activity)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)               //gets SharedPreferences
        val sessionID = preferences.getString("sessionID", "")

        Log.d("sessionActivity", "init")

        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("invite").child(userid).removeValue()

        if (ContextCompat.checkSelfPermission(this,          //checks if app has location permission
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
        }


        quitButton.setOnClickListener{
            if(sessionID == userid){ //Removes the session from the database if the user is the host.
                rtdb.child("sessionManager").child("sessionIndex").child(sessionID).removeValue()
            }
            else{ //Removes the user from a session if user is NOT the host
                rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").child(userid).removeValue()
                rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child(userid).removeValue()
            }

            this.finish()
        }  //sets function for quit button


        inviteButton.setOnClickListener{openFragment(SessionInitFragment.newInstance())}  //invite function


        //sets listener for when the session is ready to start
        val stateCheck = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("rtdb error", "lost connection to database profile updates")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //if absoluteReady for the session is true, the session is ready to start
                Log.d("sessionAct", "state checked")
                Log.d("sessionAct", dataSnapshot.value.toString())

                if(dataSnapshot.value == true) {                       //checks for any friend sessions where user is an invites player
                    Log.d("sessionActivity", "Launching session")
                    //val intent = Intent(this@SessionActivity, MyMap::class.java)
                    //android.os.SystemClock.sleep(1000)
                    //this@SessionActivity.startActivity(intent)
                    startActivity(Intent(this@SessionActivity, MyMap::class.java))
                }
            }
        }
        //starts absoluteReady listener
        rtdb.child("sessionManager").child("sessionIndex").child(sessionID)
                .child("ready").child("absoluteReady").addValueEventListener(stateCheck)


        Log.d("sessionID", sessionID)
        Log.d("userID", userid)

        //fills profile for local user
        val docRef = fsdb.collection("users").document(userid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentProfile = task.result
                localUsername.text = currentProfile!!.getString("username")
                Picasso.with(this).load(currentProfile.getString("profilepic")).fit().into(localPic)

            }
        }

        //otherUsername.text = "pending.."

        openFragment(ReadyUpFragment.newInstance())


        var palSelected = false

        supportFragmentManager.addOnBackStackChangedListener {

            //if ready up fragment is closed, close the session
            if(supportFragmentManager.findFragmentByTag("readyup") == null){
                //close the session
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()



    }




    override fun onResume() {
        super.onResume()




    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.sessionHolder, fragment, "readyup")
        transaction.addToBackStack(null)
        transaction.commit()
    }


}