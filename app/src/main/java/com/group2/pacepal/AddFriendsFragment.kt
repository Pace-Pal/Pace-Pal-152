package com.group2.pacepal

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import android.support.v4.view.MenuItemCompat.getActionView

import android.support.v7.widget.SearchView
import android.view.*
import android.support.v4.view.MenuItemCompat
import android.text.method.TextKeyListener.clear
import android.view.MenuInflater




class AddFriendsFragment : Fragment() {

    private val fsdb = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val friendsList = ArrayList<Friend>(0)
    //private val friendRequestList = ArrayList<Friend>(0)
    private val adapter = AddFriendsAdapter(friendsList)
    //private val adapterReq = FriendRequestsAdapter(friendsList)
    //private val rtdb = FirebaseDatabase.getInstance().reference

    private lateinit var friendReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //inflates the layout xml to be displayed
        val view = inflater.inflate(R.layout.fragment_add_friends, container, false)

        //initializes the recyclerView with its adapter
        val invView = view.findViewById(R.id.addFriendsList) as RecyclerView
        invView.layoutManager = LinearLayoutManager(this.context)
        invView.adapter = adapter


        //refresh button to refresh friends list
        val refreshButton = view.findViewById<Button>(R.id.friendsRefreshAdd)
        refreshButton.setOnClickListener {
            val friendsFragment = FriendsFragment()

            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frameLayout, friendsFragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()

        }

        //Button to find and add friends
        val addFriendsBtn = view.findViewById<Button>(R.id.addFriendsBtnAdd)
        addFriendsBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                addFriends("")
            }
        })

        //Button to get to the friends request page.
        val friendRequestBtn = view.findViewById<Button>(R.id.friendRequestsBtnAdd)
        friendRequestBtn.setOnClickListener{
            val friendRequests = FriendRequestFragment()

            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frameLayout, friendRequests)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()

        }

        //initial load of requests
        addFriends("")

        return view
    }

    companion object {
        fun newInstance(): FriendRequestFragment = FriendRequestFragment()
    }

    private fun addFriends(nameSearch: String){
        friendsList.clear() //starting here on updates

        //inviteRefresh.text = "loading.."
        val intentContext = this.context!!
        val friendsFromFS = fsdb.collection("users")

        friendsFromFS.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.size() == 0)
                            Toast.makeText(context, "No requests at this time!", Toast.LENGTH_SHORT).show()
                        for (document in task.result!!) {


                            var friendGet = fsdb.collection("users").document(document.id)

                            friendGet.get().addOnSuccessListener { friendProfile ->

                                if(nameSearch == ""){
                                    friendsList.add(Friend(
                                            friendProfile.getString("profilepic").toString(),
                                            friendProfile.getString("username").toString(),
                                            friendProfile.getString("first") + " " + friendProfile.getString("last"),
                                            document.id,
                                            1,
                                            intentContext
                                    ))
                                }
                                else {

                                }
                                adapter.notifyDataSetChanged()
                            }
                        }

                    }
                    else
                        Toast.makeText(context,"Connection error.", Toast.LENGTH_SHORT)
                }


    }


}