package com.group2.pacepal

import com.google.firebase.firestore.FirebaseFirestore

data class chatChannelData (val userIds: MutableList<String>) {
    constructor(): this(mutableListOf())
}

//TODO: Add these functions to chat fragment once it is ready. Maybe after context is figured out.
/*
//for calling chat channel collection
//private val chatChannelsCollectionRef = FirebaseFirestore.getInstance().collection("chatChannels")

//function for creating chat channels to use in chatActivity later (or maybe copy his basic utils section to expedite this
fun getOrCreateChatChannel(otheruserId: String,
                           onComplete: (channelId: String) -> Unit) {
    currentUserDocRef.collection("engagedChatChannels")
            .document(otheruserId).get().addOnSuccessListener {
                if (it.exists()) { //if a chat channel exists
                    onComplete(it["channelID"] as String )
                    return@addOnSuccessListener
                } //else create a chat channel
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                        .newChannel.set(ChatChannel(mutableListOf(currentUserId, otheruserId)))

                currentUserDocRef.collection("engagedChatChannels")
                         .document(otheruserId)
                         .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId)
                        .collection("engagedChatChannels")
                        .document(currentUserId)
                        .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }

            }

fun addChatMessagesListener(channelId: String, context: Context,
                            onListen: (List<Item>) -> Unit): ListenerRegistration {
    return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                    return@forEach
                }
                onListen(items)
            }
}

//need this too import com.google.firebase.firestore.ListenerRegistration

*/