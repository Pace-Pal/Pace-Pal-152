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






class RecyclerAdapter1 (private val invites: ArrayList<Invite>)  : RecyclerView.Adapter<RecyclerAdapter1.InviteHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.inviteview_row_item,parent,false)
        return InviteHolder(inflatedView)
    }

    override fun getItemCount() = invites.size

    override fun onBindViewHolder(holder: RecyclerAdapter1.InviteHolder, position: Int) {
        val itemInvite = invites[position]
        invites[position].feature

        holder.bindInvite(itemInvite)
    }

    class InviteHolder(v:View) : RecyclerView.ViewHolder(v) , View.OnClickListener {

        private var view : View = v
        private var invite : Invite? = null

        init {
            v.setOnClickListener {this}
        }

        override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK!")

            }

        fun bindInvite(invite: Invite) {
            this.invite = invite
            val displayText = invite.host + " : " + invite.type
            view.inviteButton.text = displayText
            val preferences = PreferenceManager.getDefaultSharedPreferences(invite.feature)
            val editor = preferences.edit()
            editor.putString("sessionID", invite.hostID)
            editor.putString("friendUID", invite.hostID)
            editor.putBoolean("initState", true)
            editor.commit()
            view.inviteButton.setOnClickListener{
                val parentContext = invite.feature
                val intent = Intent(parentContext, SessionActivity::class.java)
                parentContext.startActivity(intent)
            }



        }
    }
}