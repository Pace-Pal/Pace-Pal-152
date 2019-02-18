package com.group2.pacepal

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.messageview_row_item.view.*
import java.text.SimpleDateFormat

internal class messageAdapter constructor( private var messages: ArrayList<TextMessage>): RecyclerView.Adapter<messageAdapter.messageHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): messageHolder { //try creatig an if statement to tell the adapter to use a different view depending upon context later maybe
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.messageview_row_item, parent, false)  //TODO: figure out how context will be passed around
        return messageHolder(inflatedView)
    }

    override fun onBindViewHolder(message: messageAdapter.messageHolder, position: Int) {
        val messageItem = messages[position]
        //messages[position].context

        message.bindMessage(messageItem)
    }

    override fun getItemCount() = messages.size


    class messageHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var message: TextMessage? = null


        init {
            v.setOnClickListener { this }
        }

        /*override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK!")       //delete if selecting friend works


        }*/
        //TODO: Create a message on the database to show that it works
        //TODO: Implement a database listener so the view automatically refreshes when a new message arrives (don't know if that goes here)
        fun bindMessage(message: TextMessage) { //TODO: Bind views to test
            this.message = message
            view.messageText.text = message.text

            val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            view.messageTextTime.text = dateFormat.format(message.time)
            //Picasso.with(view.context).load(friend.profilePvictureURL).fit().into(view.profilePic)

        }
    }

}

