package com.group2.pacepal

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Toast


class SettingsPageFragment : Fragment() {

    val stringList = arrayOf("Account Settings", "Change Profile Picture", "Text To Speech On/Off")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //inflates the layout xml to be displayed
        val view = inflater.inflate(R.layout.fragment_settings_page, container, false)

        val list = view.findViewById<ListView>(R.id.settingListView)
        //listView.
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, stringList)
        list.adapter = adapter

        //For text to speech values
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = preferences.edit()
        editor.putString("TTSValue", "false")
        editor.commit()

        list.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 0){
                    val accountSettings = AccountSettingsFragment()

                    val fragmentTransaction = fragmentManager?.beginTransaction()
                    fragmentTransaction?.replace(R.id.frameLayout, accountSettings)
                    fragmentTransaction?.addToBackStack(null)
                    fragmentTransaction?.commit()
                }
                else if (position==1){
                    val intent = Intent(context, ProfileImagesActivity::class.java)
                    startActivity(intent)
                }
                else if(position==2){ //add value and toast. Value is on or off. Toast states which is which.
                    var value = preferences.getString("TTSValue", null)
                    if (value == "true") {
                        editor.putString("TTSValue", "false")
                        editor.commit()
                        Toast.makeText(activity, "TTS Off", Toast.LENGTH_SHORT).show()
                    } else {
                        editor.putString("TTSValue", "true")
                        editor.commit()
                        Toast.makeText(activity, "TTS ON", Toast.LENGTH_SHORT).show()
                    }

                }

            }

        }


        return view
    }



}
