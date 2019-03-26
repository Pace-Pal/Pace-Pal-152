package com.group2.pacepal

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.group2.pacepal.R.menu.navigation
import kotlinx.android.synthetic.main.main_menu.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.drawer_menu.*


class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    //lateinit var toolbar: ActionBar

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
    } */

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_friends -> {
                //toolbar.title = "Friends"
                val friendsFragment = FriendsFragment.newInstance()
                openFragment(friendsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                //toolbar.title = "Home"
                val sessionFragment = SessionFragment.newInstance()
                openFragment(sessionFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                //toolbar.title = "Profile"
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
        setContentView(R.layout.app_bar_main)
        //toolbar = supportActionBar!!
        val navigation: BottomNavigationView = findViewById(R.id.navigationView)
        setSupportActionBar(toolbar)

        /*
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        */

        //nav_view.setNavigationItemSelectedListener(this)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val sessionFragment = SessionFragment.newInstance()
        openFragment(sessionFragment)



    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> Toast.makeText(this, "Settings Selected" , Toast.LENGTH_SHORT).show()
            R.id.signOutBtn -> signOut()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        return true
    }




    fun signOut(){
        Toast.makeText(applicationContext, "Signing Out!", Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()

        //finishes the current activity and starts the login activity.
        //finishes to prevent user from attempting to go back to profile when already signed out.
        this.finish()
        val startIntent = Intent(this, MainActivity::class.java)
        startActivity(startIntent)
    }


}

