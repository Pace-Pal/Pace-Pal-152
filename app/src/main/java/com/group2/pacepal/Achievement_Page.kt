package com.group2.pacepal

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.user_profile.*
import java.util.*

class Achievement_Page : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val userid = user!!.uid
    var miles = 5
    var textViewList = ArrayList<TextView>(0)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.achievements_page)



        db.collection("users").document(userid).collection("Achievements")
                .get()
                .addOnSuccessListener { documents ->
                    var item: Achievement
                    for (document in documents) {

                        var temp = document.toObject(Achievement::class.java)
                        var tempView: TextView
                        tempView = TextView(this)
                        AchievementPanel.addView(tempView)
                        tempView.layoutParams.height = 1000
                        tempView.layoutParams.width = 1000
                        tempView.text = temp.Title //achievement.Points
                        textViewList.add(tempView)

                    }
                }




    }


   /* fun getMiles() {
        db.collection("users").document(userid).
                get().addOnSuccessListener { document ->
            if (document != null) {
                miles = document.get("miles").toString().toInt()
            }


            db.collection("users").document(userid).collection("Achievements")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            var temp = document.toObject(Achievement::class.java)
                            AchievementList.add(temp)
                            Log.v("silly", "heeey" + miles)

                        }


                        if ((miles >= 0)) {
                            val achievementView1 = achievement_view
                            achievementView1.show("Noob", "You succesfully  joined the app :)")
                        }

                        if ((miles == 3)) {
                            val achievementView1 = achievement_view
                            achievementView1.show("You doin OK", "You have a lifetime distance of 3 miles.")
                        }

                        if (miles == 5 && isNotElem(AchievementList, "Master of Athletes") ) {
                            val achievementView1 = achievement_view
                            achievementView1.show("Master of Athletes", "5 miles ran. Not amazing, but we'll make you feel good.")
                            //Add achievement to db
                        }




                    }



        }


    }

    fun isNotElem (a : ArrayList<Achievement>, title : String) : Boolean {
        var value = false
        for(achievement in a) {
            if (achievement.Title == title) {
                return false
            }

        }
        return true

    } */


}