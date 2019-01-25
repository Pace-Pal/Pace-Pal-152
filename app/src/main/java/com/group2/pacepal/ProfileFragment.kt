package com.group2.pacepal

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
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser


        //findViewByID(R.id.profileUsername) =

        //val tv = findViewById(R.id.profileUsername) as TextView
        //tv.text = "this string is set dynamically from java code"

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
                    profileRealName.text = currentProfile.get("first").toString() + " " + currentProfile.get("last").toString()
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



        return inflater.inflate(R.layout.user_profile, container, false)
    }

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }
}
