package com.group2.pacepal

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileSettingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileSettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileSettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //inflates the layout xml to be displayed
        //val view = inflater.inflate(R.layout.fragment_profile_setting, container, false)

        return inflater.inflate(R.layout.fragment_profile_setting, container, false)
    }



}
