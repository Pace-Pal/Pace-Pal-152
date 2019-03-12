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
import kotlinx.android.synthetic.main.addfriendsview_row_item.view.*
import kotlinx.android.synthetic.main.friendrequestview_row_item.view.*
import kotlinx.android.synthetic.main.friends_list.view.*


internal class AddFriendsAdapter constructor (private var friends: ArrayList<Friend>): RecyclerView.Adapter<AddFriendsAdapter.FriendHolder>() {



    //val friendAdaptorCommunication = com.group2.pacepal.friendAdaptorCommunication



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder{ //try creatig an if statement to tell the adapter to use a different view depending upon context later maybe
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.addfriendsview_row_item,parent,false)



        return FriendHolder(inflatedView, parent)
    }

    override fun getItemCount() = friends.size

    override fun onBindViewHolder(friend: AddFriendsAdapter.FriendHolder, position: Int) {
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
            view.uNameAdd.text = friend.userName
            Log.d("friendListRname", friend.realName)
            view.realNameAdd.text = friend.realName

            Picasso.with(view.context).load(friend.profilePictureURL).fit().into(view.profilePicAdd)


            //Button handler for accepting friend requests
            view.addBtn.setOnClickListener(object : View.OnClickListener{
                val friendName = friend.userName.toString()
                val friendUserId = friend.uid.toString()
                val currentUser = userid.toString()

                override fun onClick(v: View?){

                    Toast.makeText(par.context, "Requested " + friendName , Toast.LENGTH_SHORT).show()

                    val newFriend = HashMap<String, Any>()
                    newFriend["friends"] = false

                    //Adds the friend who requested to the user's friends list.
                    fsdb.collection("users").document(friendUserId).collection("friendRequests").document(currentUser)
                            .set(newFriend)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                }

            })

        }
    }
}





