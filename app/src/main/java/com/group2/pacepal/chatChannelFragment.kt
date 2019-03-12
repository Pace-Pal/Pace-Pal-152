package com.group2.pacepal

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_chat_channel.*
import kotlinx.android.synthetic.main.user_profile.*
import java.util.*

/*
Purpose:This class is what is used to handle the chatChannel functionality. It defines the user's ability to edit text and send a message. It is also
        the container where two users communicate. Their messages are sent to the Firebase Firestore from this class, and shown to them by way of a
        adapter that stores a list of TextMessage data objects. When there is an update in the Firestore, the adapter is notified and the recycler
        view is updated to store the new message. This class also creates new chat channels between users if one does not yet already exist.

        TODO: Chat room messages all reload each time a message is sent. This is not good behavior. Better would be if the new message was added to the recycler view rather than every message.
        TODO: The app crashes when the chat is opened, used, closed, then reopened and used again. This is related to the scrollToPosition function so that will need to be addressed eventually.
 */

class chatChannelFragment: Fragment() {

    private val textMessages = ArrayList<TextMessage>(0)
    private val adapter = messageAdapter(textMessages)
    private lateinit var messagesListenerRegistration: ListenerRegistration //will attach to other user's document to notify client of database message updates
    private val fsdb = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val userid = user!!.uid
    private val dbChatChannels = fsdb.collection("chatChanels") //mispelled it, whoops


    //current user we want to use for later
    private val currentUserDocRef: DocumentReference
        get() = FirebaseFirestore.getInstance().document("users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = FirebaseFirestore.getInstance().collection("chatChanels")
    private lateinit var currentChannelId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{


        //grab the bundled arguments passed in from the friendsAdapter , activityType == 1
        val friendUID = arguments!!.getString("friend_uid")
        val friendUsername = arguments!!.getString("friend_userName")
        val friendRealName= arguments!!.getString("friend_real_name")

        Log.d("New Fragemnt", "chat channel success")

        //create fragment view
        val view =  inflater.inflate(R.layout.fragment_chat_channel, container, false)
        // val imgView_send  =  findViewById(R.id.imageView_send) as ImageView





        //initializes the recyclerView with its adapter
        val invView = view?.findViewById(R.id.messageList) as RecyclerView
        invView.layoutManager = LinearLayoutManager(this.context)
        invView.adapter = adapter

        val contexts = context!! //big what?

        adapter.notifyDataSetChanged()

        //testing this to pass current context of the app to the rest of everything


        //want to attach a listener to the friend's chatChannel so clientside knows to update the client when the database updates
        val otherUserId = friendUID!!
        getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration = addChatMessagesListener(channelId, contexts, this::updateRecyclerView) //where it gets different

            imageView_send.setOnClickListener {
                val messageToSend =
                        TextMessage(editText_message.text.toString(), Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                otherUserId, "test_name") //todo: Change test name
                editText_message.setText("")
                sendMessage(messageToSend, channelId)
            }

            /*
            fab_send_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            } */ */
        }


    //}



        return view
    }

    //get or create chat channels (from ResoCoder)
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
                    newChannel.set(chatChannelData(mutableListOf(currentUserId, otheruserId)))

                    currentUserDocRef.collection("engagedChatChannels")
                            .document(otheruserId)
                            .set(mapOf("channelID" to newChannel.id)) //a newChannel.id is a id to refer to the document we just created in the chatChanels collection

                    fsdb.collection("users").document(otheruserId) //todo: change this document
                            .collection("engagedChatChannels")
                            .document(currentUserId)
                            .set(mapOf("channelID" to newChannel.id))

                    onComplete(newChannel.id)
                }

    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (ArrayList<TextMessage>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
                .orderBy("time")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = ArrayList<TextMessage>()
                    querySnapshot!!.documents.forEach {
                        if (it["type"] == MessageType.TEXT)
                            items.add(it.toObject(TextMessage::class.java)!!)
                        return@forEach
                    }
                    onListen(items)
                }
    }

    //This function will
    fun updateRecyclerView(messages:ArrayList<TextMessage>) {
        //want to add the list of text messages that are not already in the list of ArrayList<TextMessage> so that they do not all reload
        Log.v("Listener Active", "The listener is active")
        val count = 0
        //basic version though
        for(i in messages) {   // update the current TextMessage Adapter to have the new messages (I am not getting rid of the old yet)

                textMessages.add(i)

        }
        //val size = messages.size
       messageList.scrollToPosition(messageList.adapter!!.itemCount - 1) //todo: ensure we don't get the crash which means we need the adapter to be never empty I think.
       /* if(size > 0)
            textMessages.add(messages[size-1])
        else
            textMessages.add(messages[0]) */
        adapter.notifyDataSetChanged()  //notify the adapter that we have a change so that we can display the new text messages
    }

    fun checkUniqueMessage() {
        //body here
    }

    fun sendMessage(message:TextMessage,  channelId: String) {
        chatChannelsCollectionRef.document(channelId)
                .collection("messages")
                .add(message)
    }






}