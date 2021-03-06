package com.group2.pacepal

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.friendview_row_item.view.*
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_profile.view.*
import java.security.AccessController.getContext
import android.os.Bundle
import android.support.constraint.Constraints.TAG
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


internal class FriendsAdapter constructor (private var friends: ArrayList<Friend>): RecyclerView.Adapter<FriendsAdapter.FriendHolder>() {



    //val friendAdaptorCommunication = com.group2.pacepal.friendAdaptorCommunication



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder{ //try creatig an if statement to tell the adapter to use a different view depending upon context later maybe
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.friendview_row_item,parent,false)



        return FriendHolder(inflatedView, parent)
    }

    override fun getItemCount() = friends.size

    override fun onBindViewHolder(friend: FriendsAdapter.FriendHolder, position: Int) {
        val itemFriend = friends[position]
        friends[position].feature


        friend.bindFriend(itemFriend)
    }

    /*override fun onClick(v: View?) {
        Log.d("RecyclerView", "CLICK!")
        callback.invoke()

    }*/

    class FriendHolder(v:View, parent: ViewGroup) : RecyclerView.ViewHolder(v){
        private val fsdb = FirebaseFirestore.getInstance()
        private val user = FirebaseAuth.getInstance().currentUser
        private val userid = user!!.uid

        private var view : View = v
        private var friend : Friend? = null
        private var par: ViewGroup = parent



        init {
            v.setOnClickListener {this}
        }

        /*override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK!")       //delete if selecting friend works


        }*/

        fun bindFriend(friend: Friend) {
            this.friend = friend

            Log.d("friendListUname", friend.userName)
            view.uName.text = friend.userName
            Log.d("friendListRname", friend.realName)
            view.realName.text = friend.realName

            Picasso.with(view.context).load(friend.profilePictureURL).fit().into(view.profilePic)

            view.friendSelection.setOnClickListener{
                if(friend.activityType == 1){                  //case for view in friends list

                    val myFragment = friendProfileFragment()
                    val bundle = Bundle()
                    bundle.putString("friend_uid", friend.uid)
                    bundle.putString("friend_userName", friend.userName)
                    bundle.putString("friend_real_name", friend.realName)

                    myFragment.setArguments(bundle)

                    val activity = view.context as AppCompatActivity
                    activity.supportFragmentManager.beginTransaction().replace(R.id.frameLayout, myFragment).addToBackStack(null).commit() //test

                }else if(friend.activityType == 2){        //case for view in


                    val preferences = PreferenceManager.getDefaultSharedPreferences(friend.feature.applicationContext)
                    val editor = preferences.edit()
                    editor.putBoolean("initState", true)

                    val rtdb = FirebaseDatabase.getInstance().reference

                    val sessionID = preferences.getString("sessionID", "")
                    val sessionType = preferences.getString("sessionType","")

                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("invite").child(friend.uid).child("invited").setValue(true)



                    //inits new players locations
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").child(friend.uid).child("long").setValue(0.0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").child(friend.uid).child("lat").setValue(0.0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").child(friend.uid).child("distance").setValue(0.0)

                    //inits new players ready up
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child(friend.uid).setValue(false)





                    //safe to delete after new session
                    /* rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("P2").setValue(friend.uid)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("type").setValue(sessionType)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("absoluteReady").setValue(false)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("p1Ready").setValue(false)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("p2Ready").setValue(false)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1distance").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1long").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1lat").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2distance").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2long").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2lat").setValue(0) */

                    editor.commit()

                    val manager = (friend.feature as FragmentActivity)

                    manager.supportFragmentManager.popBackStack()
                    //manager.supportFragmentManager.popBackStack()


                    //val transaction =
                    //getActivity(friend.feature).getSupportFragmentManager().beginTransaction().remove(friend.feature).commit()
                }

                Log.d("friendAdapter", "ran as " + friend.activityType)
            }

            //test to make chat channel open
            view.open_chat_channel.setOnClickListener() {

                    val myFragment = chatChannelFragment()
                    val bundle = Bundle()
                    bundle.putString("friend_uid", friend.uid)
                    bundle.putString("friend_userName", friend.userName)
                    bundle.putString("friend_real_name", friend.realName)


                    myFragment.setArguments(bundle)
                    //refers to the primary activity's (the mainActivity2) context to create a new fragement within a fragment and still
                    //keep track of the fragemnt's position on the stack
                    //potentially buggy so we can take more looks at this in the future.
                    val activity = view.context as AppCompatActivity
                    activity.supportFragmentManager.beginTransaction().replace(R.id.frameLayout, myFragment).addToBackStack(null).commit() //test



                }


            view.unfriendBtn.setOnClickListener(object : View.OnClickListener{
                val friendName = friend.userName.toString()
                val friendUserId = friend.uid.toString()
                val currentUser = userid.toString()

                //find a way to
                override fun onClick(v: View?){
                    Toast.makeText(par.context, "Unfriending " + friendName , Toast.LENGTH_SHORT).show()

                    //Removes user's friend from his/hers friends list
                    fsdb.collection("users").document(currentUser).collection("friends").document(friendUserId)
                            .delete()
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }


                    //Removes the user from the friend's friends list
                    fsdb.collection("users").document(friendUserId).collection("friends").document(currentUser)
                            .delete()
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }


                }



            })
        }


    }



}

private fun Button.setOnClickListener() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
