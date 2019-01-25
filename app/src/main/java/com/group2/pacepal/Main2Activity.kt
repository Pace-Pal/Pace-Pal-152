package com.group2.pacepal

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import com.group2.pacepal.R.menu.navigation
import kotlinx.android.synthetic.main.main_menu.*
import com.google.firebase.firestore.FirebaseFirestore



class Main2Activity : AppCompatActivity() {


    lateinit var toolbar: ActionBar

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
    } */

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_friends -> {
                toolbar.title = "Friends"
                val friendsFragment = FriendsFragment.newInstance()
                openFragment(friendsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                toolbar.title = "Home"
                val sessionFragment = SessionFragment.newInstance()
                openFragment(sessionFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                toolbar.title = "Profile"
                val profileFragment = ProfileFragment.newInstance()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }


        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        toolbar = supportActionBar!!
        val navigation: BottomNavigationView = findViewById(R.id.navigationView)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val sessionFragment = SessionFragment.newInstance()
        openFragment(sessionFragment)



    }


}