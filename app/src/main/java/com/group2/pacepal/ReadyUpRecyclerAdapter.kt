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






class ReadyUpRecyclerAdapter (private val readys: ArrayList<String>)  : RecyclerView.Adapter<ReadyUpRecyclerAdapter.ReadyHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadyHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.readyup_row_item,parent,false)
        return ReadyHolder(inflatedView)
    }

    override fun getItemCount() = readys.size

    override fun onBindViewHolder(holder: ReadyUpRecyclerAdapter.ReadyHolder, position: Int) {
        val itemReady = readys[position]
        //readys[position]

        holder.bindInvite(itemReady)
    }

    class ReadyHolder(v: View) : RecyclerView.ViewHolder(v) , View.OnClickListener {

        private var view : View = v
        private var ready : String? = null

        init {
            v.setOnClickListener {this}
        }

        override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK!")

        }

        fun bindInvite(ready: String) {
            //lookup profile picture from database and set to view
            //lookup username too
            //set listener for status



        }
    }
}