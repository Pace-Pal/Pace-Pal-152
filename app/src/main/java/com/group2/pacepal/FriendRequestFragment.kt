package com.group2.pacepal

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore



class FriendRequestFragment : Fragment() {

    private val fsdb = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val friendsList = ArrayList<Friend>(0)
    //private val friendRequestList = ArrayList<Friend>(0)
    private val adapter = FriendRequestAdapter(friendsList)
    //private val adapterReq = FriendRequestsAdapter(friendsList)
    //private val rtdb = FirebaseDatabase.getInstance().reference

    private lateinit var friendReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //inflates the layout xml to be displayed
        val view = inflater.inflate(R.layout.fragment_friend_request, container, false)

        //initializes the recyclerView with its adapter
        val invView = view.findViewById(R.id.friendRequestList) as RecyclerView
        invView.layoutManager = LinearLayoutManager(this.context)
        invView.adapter = adapter


        //refresh button to refresh friends list
        val refreshButton = view.findViewById<Button>(R.id.friendsRefreshR)
        refreshButton.setOnClickListener {
            val friendsFragment = FriendsFragment()

            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.container, friendsFragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()

        }

        val addFriendsBtn = view.findViewById<Button>(R.id.addFriendsBtnR)
        addFriendsBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                val addFriendsFragment = AddFriendsFragment()

                val fragmentTransaction = fragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.container, addFriendsFragment)
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
        })

        val friendRequestBtn = view.findViewById<Button>(R.id.friendRequestsBtnR)
        friendRequestBtn.setOnClickListener{
            friendRequests()
        }

        //initial load of requests
        friendRequests()

        return view
    }

    companion object {
        fun newInstance(): FriendRequestFragment = FriendRequestFragment()
    }



    private fun friendRequests(){
        friendsList.clear() //starting here on updates
        //inviteRefresh.text = "loading.."
        val intentContext = this.context!!
        val friendsFromFS = fsdb.collection("users").document(userid).collection("friendRequests")
        friendsFromFS.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.size() == 0)
                            Toast.makeText(context, "No requests at this time!", Toast.LENGTH_SHORT).show()
                        for (document in task.result!!) {

                            val friendGet = fsdb.collection("users").document(document.id)
                            friendGet.get().addOnSuccessListener { friendProfile ->

                                friendsList.add(Friend(
                                        friendProfile.getString("profilepic").toString(),
                                        friendProfile.getString("username").toString(),
                                        friendProfile.getString("first") + " " + friendProfile.getString("last"),
                                        document.id,
                                        1,
                                        intentContext
                                ))
                                adapter.notifyDataSetChanged()

                            }

                        }
                        //adapter.notifyDataSetChanged()

                    }
                    else
                        Toast.makeText(context,"Connection error.", Toast.LENGTH_SHORT)
                }


    }


}