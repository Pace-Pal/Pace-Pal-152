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
import kotlinx.android.synthetic.main.friendrequestview_row_item.view.*


internal class FriendRequestAdapter constructor (private var friends: ArrayList<Friend>): RecyclerView.Adapter<FriendRequestAdapter.FriendHolder>() {



    //val friendAdaptorCommunication = com.group2.pacepal.friendAdaptorCommunication



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder{ //try creatig an if statement to tell the adapter to use a different view depending upon context later maybe
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.friendrequestview_row_item,parent,false)



        return FriendHolder(inflatedView, parent)
    }

    override fun getItemCount() = friends.size

    override fun onBindViewHolder(friend: FriendRequestAdapter.FriendHolder, position: Int) {
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

        fun bindFriend(friend: Friend) {
            this.friend = friend

            Log.d("friendListUname", friend.userName)
            view.uNameR.text = friend.userName
            Log.d("friendListRname", friend.realName)
            view.realNameR.text = friend.realName

            Picasso.with(view.context).load(friend.profilePictureURL).fit().into(view.profilePicReq)


            //Button handler for accepting friend requests
            view.acceptBtn.setOnClickListener(object : View.OnClickListener{
                val friendName = friend.userName.toString()
                val friendUserId = friend.uid.toString()
                val currentUser = userid.toString()

                override fun onClick(v: View?){

                    Toast.makeText(par.context, "Accepted " + friendName , Toast.LENGTH_SHORT).show()

                    val newFriend = HashMap<String, Any>()
                    newFriend["friends"] = true

                    //Adds the friend who requested to the user's friends list.
                    fsdb.collection("users").document(currentUser).collection("friends").document(friendUserId)
                            .set(newFriend)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                    //Adds the user to the requested friend's friends list.
                    fsdb.collection("users").document(friendUserId).collection("friends").document(currentUser)
                            .set(newFriend)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                    //Removes the request from the user's requests list
                    fsdb.collection("users").document(currentUser).collection("friendRequests").document(friendUserId)
                            .delete()
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                }

            })



            //Button handler for declining friend requests.
            view.declineBtn.setOnClickListener(object : View.OnClickListener{
                val friendName = friend.userName.toString()
                val friendUserId = friend.uid.toString()
                val currentUser = userid.toString()

                override fun onClick(v: View?){

                    Toast.makeText(par.context, "Declined " + friendName , Toast.LENGTH_SHORT).show()

                    //Removes the request from the user's requests list
                    fsdb.collection("users").document(currentUser).collection("friendRequests").document(friendUserId)
                            .delete()
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                }

                val friendRequests = FriendRequestFragment()

            })
        }
    }
}





