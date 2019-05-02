package com.group2.pacepal

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.imgmessageview_row_item.view.*
import kotlinx.android.synthetic.main.messageview_row_item.view.*
import kotlinx.android.synthetic.main.messageview_row_item_otheruser.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class messageAdapterMultipleViews ( private var messages : ArrayList<TextMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    companion object {
        const val USER_MESSAGE = 1
        const val OTHER_USER_MESSAGE = 2
        const val USER_IMG_MESSAGE = 3
        const val OTHER_USER_IMG_MESSAGE = 4
    }

    override fun getItemViewType(position: Int): Int {
        val type : Int
        if (messages[position].senderId  == FirebaseAuth.getInstance().currentUser!!.uid) { //TODO: Better way than this find it
            //Log.v("imgPL", "Value: " + messages[position].imagePath.length)
            if(messages[position].imagePath.length == 1) {
                //Log.v("TxtU", "Weeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                type = USER_MESSAGE
            } else {
                //Log.v("ImgU", "Weeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                type = USER_IMG_MESSAGE
            }
            //Log.v("IMUW", "App says I am me")
        } else {

            if(messages[position].imagePath.length == 1) {
                type = OTHER_USER_MESSAGE
            } else {
                type = OTHER_USER_IMG_MESSAGE
                //Log.v("ImgMOU", "Weeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
            }
            //Log.v("IMUW", "App says I am not me")
        }
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            USER_MESSAGE -> UserMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.messageview_row_item, parent, false))
            // other view holders...

            USER_IMG_MESSAGE -> UserImgMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.imgmessageview_row_item, parent, false))

            OTHER_USER_MESSAGE -> OtherUserMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.messageview_row_item_otheruser, parent, false))

            OTHER_USER_IMG_MESSAGE -> OtherUserImgMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.imgmessageview_row_item, parent, false))

            else ->  UserMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.imgmessageview_row_item, parent, false)) //make other user
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) { //TODO: Used to be ViewHolder?
        (holder as UpdateViewHolder).bindViews(messages.get(position))

        //Log.v("Adapter I Pos", "Value is: " + (position))
    }


    override fun getItemCount() = messages.size


    interface UpdateViewHolder {
        fun bindViews(message: TextMessage)
    }

// other view holders...

    class UserMessageViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), UpdateViewHolder {

        // get the views reference from itemView
        private var view: View = itemView
        private var message: TextMessage? = null

        override fun bindViews(message: TextMessage) {
            //val newMessage = message as TextMessage
            this.message = message
            view.messageText.text = message.text
            val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            view.messageTextTime.text = dateFormat.format(message.time)
            Log.v("IMUC", "In User binding the message to right side")

        }
    }

    class OtherUserMessageViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), UpdateViewHolder {

        // get the views reference from itemView... TODO: might not be doing this. whoops if so.

        private var view: View = itemView
        private var message: TextMessage? = null

        override fun bindViews(message: TextMessage) {
            //this.message = message
            //val newMessage = message as TextMessage
            view.messageTextOtherUser.text = message.text
            val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            view.messageTextTimeOtherUser.text = dateFormat.format(message.time)
            Log.v("IMOUC", "In otherUser binding the message to left side")
        }
    }

    //fun pathToReference(path: String) = storageInstance.getReference(path)

    class UserImgMessageViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), UpdateViewHolder {
        private var view: View = itemView
        private var message: TextMessage? = null
        val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }


        override fun bindViews(message: TextMessage) {
            //this.message = message

            Log.v("ImgP", "i" + message.imagePath)
            GlideApp.with(view.context)
                    .load(storageInstance.getReference(message.imagePath))
                    .placeholder( R.drawable.ic_send_black_24dp)
                    .into(view.imageView_message_image)
        }
    }


    class OtherUserImgMessageViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), UpdateViewHolder {
        private var view: View = itemView
        private var message: TextMessage? = null
        val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }


        override fun bindViews(message: TextMessage) {
            //this.message = message

            Log.v("ImgP", "i" + message.imagePath)
            GlideApp.with(view.context)
                    .load(storageInstance.getReference(message.imagePath))
                    .placeholder( R.drawable.ic_send_black_24dp)
                    .into(view.imageView_message_image)
        }
    }




}