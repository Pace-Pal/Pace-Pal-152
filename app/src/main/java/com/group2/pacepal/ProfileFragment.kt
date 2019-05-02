package com.group2.pacepal

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.android.synthetic.main.user_profile
import kotlinx.android.synthetic.main.user_profile.*
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.FirebaseAuth
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.firebase.firestore.DocumentSnapshot
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.text.Layout
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.cdev.achievementview.AchievementView
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Tasks
import kotlinx.android.synthetic.*
import org.w3c.dom.Text
import java.util.*
import kotlin.math.sign


class ProfileFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    var AchievementList = ArrayList<Achievement>(0)
    var AchievementListT = ArrayList<String>(0)
    var textViewList = ArrayList<TextView>(0)
    lateinit var textView : TextView
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val userid = user!!.uid
    var miles = 5


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {





        //findViewByID(R.id.profileUsername) =

        //val tv = findViewById(R.id.profileUsername) as TextView
        //tv.text = "this string is set dynamically from java code"
        //val view = inflater.inflate(R.layout.user_profile, container, false)

        val userid = user!!.uid

        val docRef = db.collection("users").document(userid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentProfile = task.result
                if (currentProfile!!.exists()) {
                    //Log.d(TAG, "DocumentSnapshot data: " + document.data!!)
                    profileUsername.text = currentProfile.getString("username")
                    profileMiles.text = currentProfile.getDouble("miles").toString()
                    profileChallenges.text = currentProfile.get("challenges").toString()
                    profileRealName.text = currentProfile.get("first").toString() + " " + currentProfile?.get("last").toString()
                    Picasso.with(context).load(currentProfile.getString("profilepic")).fit().into(profileImage)


                    val firebasedb = db.collection("users").document(userid).collection("friends")
                    firebasedb.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var friendsCount = 0
                            //profileFriends.text = task.result!!.size().toString()
                            for (document in task.result!!) {
                                friendsCount++
                            }
                            profileFriends.text = friendsCount.toString()
                        } else {
                            profileFriends.text = "Na"
                        }
                    }


                } else {
                    profileUsername.text = "NaNa"
                    profileMiles.text = "NaNa"
                    profileChallenges.text = "NaNa"
                    profileRealName.text = "NaNa"
                }
            } else {
                profileUsername.text = "Na"
                profileMiles.text = "Na"
                profileChallenges.text = "Na"
                profileRealName.text = "Na"
            }

        }

        //var achievementPanelVal = AchievementPanel

        //Dynamic addition test



        val view = inflater.inflate(R.layout.user_profile, container, false)



        return view
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
<<<<<<< HEAD
        super.onViewCreated(view, savedInstanceState)


        var docRef =  db.collection("users").document(userid)



=======
         super.onViewCreated(view, savedInstanceState)
         val userid = user!!.uid
>>>>>>> b15a71b5c170fb9339b27b4e3770c84c732f9d3e


         db.collection("users").document(userid).collection("Achievements")
                 .get()
                 .addOnSuccessListener { documents ->
                     var item: Achievement
                     for (document in documents) {

                         var temp = document.toObject(Achievement::class.java)
                         var tempView: TextView
                         tempView = TextView(activity)
                         AchievementPanel.addView(tempView)
                         tempView.layoutParams.height = 1000
                         tempView.layoutParams.width = 1000
                         tempView.text = temp.Title //achievement.Points
                         textViewList.add(tempView)

                     }
                 }


/*
         val docRef = db.collection("users").document(userid)
         docRef.get().addOnCompleteListener { task ->
             if (task.isSuccessful) {
                 val currentProfile = task.result
                 if (currentProfile!!.exists()) {
                     var myMiles = currentProfile.getDouble("miles").toString()

                     if ((myMiles == "0.0")) {
                         val achievementView1 = achievement_view
                         achievementView1.show("Noob", "You succesfully  joined the app :)")
                     }
                 }
             }
         }
     }
    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    } */

    fun getMiles() {
        db.collection("users").document(userid).
                get().addOnSuccessListener { document ->
            if (document != null) {
                miles = document.get("miles").toString().toInt()
            }


            db.collection("users").document(userid).collection("Achievements")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            var temp = document.toObject(Achievement::class.java)
                            AchievementList.add(temp)
                            Log.v("silly", "heeey" + miles)

                        }

                        //given you have a total of 5 miles, you get tachievement A if it does not exists
                        //Put your logic here 
                        for(achievement in AchievementList) {
                            achievement.Title
                            return true
                        }




                    }



        }
    }



    fun (a : ArrayList <Achivemenent>) {


        //do the logic

        return false
    }

}
