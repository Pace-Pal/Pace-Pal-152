package com.group2.pacepal

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.app_bar_main.*




class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_friends -> {
                toolbar.setTitle("Friends")
                val friendsFragment = FriendsFragment.newInstance()
                openFragment(friendsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                toolbar.setTitle("Home")
                val sessionFragment = SessionFragment.newInstance()
                openFragment(sessionFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                toolbar.setTitle("Profile")
                val profileFragment = ProfileFragment.newInstance()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }


        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_main)
        //toolbar = supportActionBar!!
        val navigation: BottomNavigationView = findViewById(R.id.navigationView)
        setSupportActionBar(toolbar)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val sessionFragment = SessionFragment.newInstance()


        openFragment(sessionFragment)



    }

    override fun onBackPressed() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        toolbar.setTitle("Settings")

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> openSetting()//Toast.makeText(this, "Settings Selected" , Toast.LENGTH_SHORT).show()
            R.id.signOutBtn -> signOut()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        return true
    }



    private fun openSetting() {
        //val intent = Intent(this, UploadPictureActivity::class.java)
        //startActivity(intent)
        val profileSet = ProfileSettingFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, profileSet)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

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

