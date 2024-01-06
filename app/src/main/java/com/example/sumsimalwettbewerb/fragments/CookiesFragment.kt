package com.example.sumsimalwettbewerb.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sumsimalwettbewerb.R
import com.example.sumsimalwettbewerb.fragments.InfoDialogFragment

class CookiesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cookie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val CookiePreferencesTv = view.findViewById<TextView>(R.id.CookiePreferencesTv)
        CookiePreferencesTv.setOnClickListener {
            showCookiePreferences()
        }
    }
    private fun  showCookiePreferences(){

        val message = getString(R.string.info6)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(childFragmentManager, "InfoDialog")
    }
}
