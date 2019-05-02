package com.group2.pacepal

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*


class FriendsFragment : Fragment() {

    private val fsdb = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val friendsList = ArrayList<Friend>(0)
    //private val friendRequestList = ArrayList<Friend>(0)
    private val adapter = FriendsAdapter(friendsList)
    //private val adapterReq = FriendRequestsAdapter(friendsList)
    //private val rtdb = FirebaseDatabase.getInstance().reference

    private lateinit var friendReference: DatabaseReference

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //inflates the layout xml to be displayed
        val view = inflater.inflate(R.layout.friends_list, container, false)

        //initializes the recyclerView with its adapter
        val invView = view.findViewById(R.id.friendsList) as RecyclerView
        invView.layoutManager = LinearLayoutManager(this.context)
        invView.adapter = adapter


        //refresh button to refresh friends list
        val refreshButton = view.findViewById<Button>(R.id.friendsRefresh)
        refreshButton.setOnClickListener {
            refreshFriends("")
        }

        val friendRequestBtn = view.findViewById<Button>(R.id.friendRequestsBtn)


        friendRequestBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                //toolbar.setTitle("Requests") // DOES NOT WORK.

                val friendRequests = FriendRequestFragment()

                val fragmentTransaction = fragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.frameLayout, friendRequests)
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()


            }
        })


        val addFriendsBtn = view.findViewById<Button>(R.id.addFriendsBtn)
        addFriendsBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){


                val addFriendsFragment = AddFriendsFragment()

                val fragmentTransaction = fragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.frameLayout, addFriendsFragment)
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
        })
            /*
            val friendRequests = FriendRequestFragment()

            val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(android.R.id.content, friendRequests)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            */


        //initial load of friends list
        refreshFriends("")

        return view
    }

    companion object {
        fun newInstance(): FriendsFragment = FriendsFragment()
    }

    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater){
       //inflater.inflate(R.menu.main, menu)
        //super.onCreateOptionsMenu(menu, inflater)


        menu.findItem(R.id.action_search).setVisible(true)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                refreshFriends(newText.toLowerCase())
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

        })

    }


    private fun refreshFriends(nameSearch : String) {

        friendsList.clear() //starting here on updates



        //inviteRefresh.text = "loading.."
        val intentContext = this.context!!
        val friendsFromFS = fsdb.collection("users").document(userid).collection("friends")
        friendsFromFS.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.size() == 0)
                            Toast.makeText(context, "You need to add friends before you can ever have a list of them.", Toast.LENGTH_SHORT).show()
                        for (document in task.result!!) {

                            val friendGet = fsdb.collection("users").document(document.id)
                            friendGet.get().addOnSuccessListener { friendProfile ->

                                var friendProfilePic = friendProfile.getString("profilepic").toString()
                                var friendUserName = friendProfile.getString("username").toString()
                                var friendFirstName = friendProfile.getString("first").toString()
                                var friendLastName = friendProfile.getString("last").toString()
                                var friendFullName = friendFirstName + " " + friendLastName

                                if(friendUserName.toLowerCase().contains(nameSearch) || friendFullName.toLowerCase().contains(nameSearch)){
                                    friendsList.add(Friend(
                                            friendProfilePic,
                                            friendUserName,
                                            friendFirstName + " " + friendLastName,
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
                        //adapter.notifyDataSetChanged()

                    }
                    else
                        Toast.makeText(context,"Connection error.", Toast.LENGTH_SHORT)
                }

    }
}