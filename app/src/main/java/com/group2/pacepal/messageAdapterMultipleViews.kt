package com.group2.pacepal

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.messageview_row_item.view.*
import kotlinx.android.synthetic.main.messageview_row_item_otheruser.view.*
import java.text.SimpleDateFormat

class messageAdapterMultipleViews ( private var messages : ArrayList<TextMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val USER_MESSAGE = 1
        const val OTHER_USER_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        val type : Int
        if (messages[position].senderId  == FirebaseAuth.getInstance().currentUser!!.uid) {
            type = USER_MESSAGE
            Log.v("IMUW", "App says I am me")
        } else {
            type = OTHER_USER_MESSAGE
            Log.v("IMUW", "App says I am not me")
        }
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            USER_MESSAGE -> UserMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.messageview_row_item, parent, false))
            // other view holders...
            OTHER_USER_MESSAGE -> OtherUserMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.messageview_row_item_otheruser, parent, false))

            else ->  UserMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.messageview_row_item, parent, false))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UpdateViewHolder).bindViews(messages.get(position))

        Log.v("Adapter I Pos", "Value is: " + (position))
    }


    override fun getItemCount() = messages.size

    fun setMessageList(message: ArrayList<TextMessage>) {
        messages = message
        notifyDataSetChanged()
    }

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
            this.message = message
            //val newMessage = message as TextMessage
            view.messageTextOtherUser.text = message.text
            val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            view.messageTextTimeOtherUser.text = dateFormat.format(message.time)
            Log.v("IMOUC", "In otherUser binding the message to left side")
        }
    }




}