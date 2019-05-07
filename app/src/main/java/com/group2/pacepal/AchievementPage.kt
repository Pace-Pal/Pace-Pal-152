package com.group2.pacepal

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.achievements_page.*
import kotlinx.android.synthetic.main.user_profile.*
import com.cdev.achievementview.AchievementView
import java.util.*

class AchievementPage : AppCompatActivity () {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val userid = user!!.uid
    var miles = 5
    var textViewList = ArrayList<TextView>(0)
    var AchievementList = ArrayList<Achievement>(0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievements_page)


        db.collection("users").document(userid).collection("Achievements")
                .get()
                .addOnSuccessListener { documents ->

                    for (document in documents) {
                        Log.v("Achievement", "We have one")

                        var temp = document.toObject(Achievement::class.java)
                        var tempView: TextView
                        tempView = TextView(applicationContext)
                        AchievementPanelMain.addView(tempView)
                        tempView.layoutParams.height = 1000
                        tempView.layoutParams.width = 1000
                        tempView.text = temp.Title
                        textViewList.add(tempView)

                    }
                }


        getMiles()

    }


     fun getMiles() {
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

                         }


                         if ((miles >= 0) && isNotElem(AchievementList, "Noob")) {
                             val achievementView1 = achievement_views
                             achievementView1.show("Noob", "You succesfully  joined the app :)")
                             insertAchievement("Noob", 15)
                         }

                         if ((miles == 3) && isNotElem(AchievementList, "Super Runner")) {
                             val achievementView1 = achievement_views
                             achievementView1.show("Super Runner", "You have a lifetime distance of 3 miles.")
                             insertAchievement("Super Runner", 30)
                         }

                         if (miles == 5 && isNotElem(AchievementList, "Master of Athletes") ) {
                             val achievementView1 = achievement_views
                             achievementView1.show("Master of Athletes", "5 miles ran. Not amazing, but we'll make you feel good.")
                             insertAchievement("Master of Athletes", 9000)

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

     }

    fun insertAchievement(title : String, points : Int) {
        val achievement = HashMap<String, Any>()
        achievement["Title"] = title
        achievement["Points"] = points

        db.collection("users").document(userid).collection("Achievements")
                .add(achievement)
                .addOnSuccessListener { Log.d("AInsertS", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("AInsertF", "Error writing document", e) }
    }



}