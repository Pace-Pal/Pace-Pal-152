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




internal class FriendsAdapter constructor (private var friends: ArrayList<Friend>): RecyclerView.Adapter<FriendsAdapter.FriendHolder>() {

    //val friendAdaptorCommunication = com.group2.pacepal.friendAdaptorCommunication


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder{
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.friendview_row_item,parent,false)
        return FriendHolder(inflatedView)
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

    class FriendHolder(v:View) : RecyclerView.ViewHolder(v){

        private var view : View = v
        private var friend : Friend? = null

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
                if(friend.activityType == 1){
                    //Toast.makeText(friend.feature,"TODO: open friend profile", Toast.LENGTH_SHORT).show()
                    //openFragment(friendProfileFragment.newInstance())
                    //val preferences = PreferenceManager.getDefaultSharedPreferences(friend.feature.applicationContext)
                    //val editor = preferences.edit()
                   // editor.putString("friend_profile", friend.uid) //experiment here
                   // editor.commit()


                    val myFragment = friendProfileFragment()
                    val bundle = Bundle()
                    bundle.putString("friend_uid", friend.uid)
                    bundle.putString("friend_userName", friend.userName)
                    bundle.putString("friend_real_name", friend.realName)

                    myFragment.setArguments(bundle)
                    //refers to the primary activity's (the mainActivity2) context to create a new fragement within a fragment and still
                    //keep track of the fragemnt's position on the stack
                    //potentially buggy so we can take more looks at this in the future.
                    val activity = view.context as AppCompatActivity
                    activity.supportFragmentManager.beginTransaction().replace(R.id.container, myFragment).addToBackStack(null).commit() //test



                }else if(friend.activityType == 2){


                    val preferences = PreferenceManager.getDefaultSharedPreferences(friend.feature.applicationContext)
                    val editor = preferences.edit()
                    editor.putBoolean("initState", true)

                    val rtdb = FirebaseDatabase.getInstance().reference

                    val sessionID = preferences.getString("sessionID", "")
                    val sessionType = preferences.getString("sessionType","")

                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("P2").setValue(friend.uid)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("type").setValue(sessionType)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("absoluteReady").setValue(false)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("p1Ready").setValue(false)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("ready").child("p2Ready").setValue(false)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1distance").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1long").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1lat").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2distance").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2long").setValue(0)
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2lat").setValue(0)

                    editor.putString("friendUID",friend.uid)

                    editor.commit()

                    val manager = (friend.feature as FragmentActivity)

                    manager.supportFragmentManager.popBackStack()
                    //manager.supportFragmentManager.popBackStack()


                    //val transaction =
                    //getActivity(friend.feature).getSupportFragmentManager().beginTransaction().remove(friend.feature).commit()
                }

                Log.d("friendAdapter", "ran as " + friend.activityType)
            }



            //view.profilePic
            //val displayText = friend.userName

            /*view.friend_button.setOnClickListener{
                val parentContext = friend.feature
                view.friend_button.text = displayText
                Toast.makeText(parentContext, "WIP", Toast.LENGTH_SHORT).show(); //questionable context
               // val intent = Intent(parentContext, SessionActivity::class.java)
                //intent.putExtra("sessionID", invite.hostID)
               // parentContext.startActivity(intent)
            }*/
        }
    }



}


