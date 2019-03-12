package com.group2.pacepal

import com.google.firebase.firestore.FirebaseFirestore
/*
Purpose: This stores the userIDs that are used to indicate the members of a single chat channel. In this app, a chat channel is handled
         by a firebase Firestore channel, titled 'chatchanels' (b/c spelling is hard). This list is what fills the database with the users the
         first time a chat channel is created.
*/

data class chatChannelData (val userIds: MutableList<String>) {
    constructor(): this(mutableListOf())
}