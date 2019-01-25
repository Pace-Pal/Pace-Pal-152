package com.group2.pacepal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_profile.*

class friendProfileFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //grab the bundled arguments passed in from the friendsAdapter , activityType == 1
        val friendUID = arguments!!.getString("friend_uid")
        val friendUsername = arguments!!.getString("friend_userName")
        val friendRealName= arguments!!.getString("friend_real_name")


        val db = FirebaseFirestore.getInstance()
       // val view = inflater.inflate(R.layout.friend_profile, container, false)

        Log.d("New Fragemnt", "friend_profile success")

        //return view
        val docRef = db.collection("users").document(friendUID)
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



                    val firebasedb = db.collection("users").document(friendUID).collection("friends")
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



}
