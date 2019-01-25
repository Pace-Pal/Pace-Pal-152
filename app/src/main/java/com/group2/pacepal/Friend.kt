package com.group2.pacepal

import android.content.Context


data class Friend (val profilePictureURL: String, val userName: String, val realName: String, val uid: String, val activityType:Int, val feature: Context)

//In order
//profilePictureURL = the url for the profile picture, ideally from Firebase Storage
//userName = the requested users username
//realName = the requested users real name, ideally concatanation of 'first + " " + last' from Firestore
//uid      = the requested users UID
//activityType = the type of activity to be launched by friendsAdapter
//// 1 = opens profile, used in friends list
//// 2 = opens SessionActivity sending invite
//feature  = passes in context, im not smart enough to know that this does yet but I think its important -mason
