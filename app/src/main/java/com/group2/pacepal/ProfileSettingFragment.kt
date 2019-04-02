package com.group2.pacepal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Toast


class ProfileSettingFragment : Fragment() {

    val stringList = arrayOf("Account Settings", "Change Profile Picture")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //inflates the layout xml to be displayed
        val view = inflater.inflate(R.layout.fragment_profile_setting, container, false)

        val list = view.findViewById<ListView>(R.id.settingListView)
        //listView.
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, stringList)
        list.adapter = adapter

        list.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val intent = Intent(context, ProfileImagesActivity::class.java)
                startActivity(intent)
            }

        }


        return view
    }



}
