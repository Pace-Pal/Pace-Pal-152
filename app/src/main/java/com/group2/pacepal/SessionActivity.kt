package com.group2.pacepal

import android.Manifest
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class SessionActivity : AppCompatActivity() {

    //val sessionId = intent.getStringExtra("sessionID")
    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.session_activity)

        Log.d("sessionActivity", "init")




        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)



        }




            quitButton.setOnClickListener{this.finish()}

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val sessionID = preferences.getString("sessionID", "")
        val readyState = preferences.getBoolean("readyState", false)
        val initState = preferences.getBoolean("initState", false)
        val sessionType = preferences.getString("sessionType", "")

        Log.d("readyState", readyState.toString())
        Log.d("initState", initState.toString())
        Log.d("sessionID", sessionID)
        Log.d("userID", userid)

        val docRef = db.collection("users").document(userid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentProfile = task.result
                localUsername.text = currentProfile!!.getString("username")
                Picasso.with(this).load(currentProfile.getString("profilepic")).fit().into(localPic)

                if(userid != sessionID){
                    val docRef2 = db.collection("users").document(sessionID!!)
                    docRef2.get().addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val otherProfile = task2.result
                            otherUsername.text = otherProfile!!.getString("username")
                            Picasso.with(this).load(otherProfile.getString("profilepic")).fit().into(otherPic)
                            }
                    }
                }


            }
        }

        otherUsername.text = "pending.."


        if(sessionID == userid) {
            openFragment(ReadyUpFragment.newInstance())
            openFragment(SessionInitFragment.newInstance())
        }
        else
            openFragment(ReadyUpFragment.newInstance())

        var palSelected = false

        supportFragmentManager.addOnBackStackChangedListener {
            val readyState = preferences.getBoolean("readyState", false)
            val initState = preferences.getBoolean("initState", false)
            val friendUID = preferences.getString("friendUID", "")
            val killCommand = preferences.getBoolean("killCommand",false)

            if(killCommand)
                this.finish()

            if( !palSelected && initState && (sessionID == userid)){
                val docRef3 = db.collection("users").document(friendUID!!)
                docRef3.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val otherProfile = task.result
                        otherUsername.text = otherProfile!!.getString("username")
                        Picasso.with(this).load(otherProfile.getString("profilepic")).fit().into(otherPic)
                    }
                }

                palSelected = true
            }


            if(readyState && initState){
                val intent = Intent(this, MyMap::class.java)
                android.os.SystemClock.sleep(1000)
                this.startActivity(intent)
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        val rtdb = FirebaseDatabase.getInstance().reference

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val sessionID = preferences.getString("sessionID", "")

        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).removeValue()

        val editor = preferences.edit()
        editor.clear()
        editor.apply()


    }




    override fun onResume() {
        super.onResume()

        //openFragment(SessionInitFragment.newInstance())

       /* Log.d("sessionActivity", "resumed")

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val sessionID = preferences.getString("sessionID", "")
        val readyState = preferences.getBoolean("readyState", false)
        val initStater = preferences.getBoolean("initState", false)
        val sessionType = preferences.getString("sessionType", "")

        Log.d("readyState", readyState.toString())
        Log.d("initState", initStater.toString())
        Log.d("sessionID", sessionID)
        Log.d("userID", userid)



        if(sessionID == userid){
            if(!initStater){
                Log.d("initState", "launching Initstate fragment")
                //val initFragment = SessionInitFragment.newInstance()
                openFragment(SessionInitFragment.newInstance())
            }
            else if(!readyState){
                Log.d("initState", "launching ready fragment")
                val readyFragment = ReadyUpFragment.newInstance()
                openFragment(readyFragment)
            }
            else{
                //begin session
            }

        }
        else{
            if(!readyState){
                val readyFragment = ReadyUpFragment.newInstance()
                openFragment(readyFragment)
            }
            else{
                //begin session
            }
        }*/


    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.sessionHolder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}