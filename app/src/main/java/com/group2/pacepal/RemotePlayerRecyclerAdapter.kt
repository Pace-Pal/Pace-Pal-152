package com.group2.pacepal

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.inviteview_row_item.view.*

import kotlinx.android.synthetic.main.session_menu.view.*
import android.support.v4.content.ContextCompat.startActivity
import android.R.id.edit
import android.content.SharedPreferences
import android.preference.PreferenceManager






class RemotePlayerRecyclerAdapter (private val players: MutableList<RemotePlayer>)  : RecyclerView.Adapter<RemotePlayerRecyclerAdapter.PlayerHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.readyup_row_item,null,false)
        return PlayerHolder(inflatedView)
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: RemotePlayerRecyclerAdapter.PlayerHolder, position: Int) {
        val itemPlayer = players[position]
        //readys[position]

        holder.bindPlayer(itemPlayer)
    }

    class PlayerHolder(v: View) : RecyclerView.ViewHolder(v) , View.OnClickListener {

        private var view : View = v
        private var ready : String? = null

        init {
            v.setOnClickListener {this}
        }

        override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK!")

        }

        fun bindPlayer(ready: RemotePlayer) {
            //lookup profile picture from database and set to view
            //lookup username too
            //set listener for status

            Log.d("RPrecycler","doest this even run")

        }
    }
}