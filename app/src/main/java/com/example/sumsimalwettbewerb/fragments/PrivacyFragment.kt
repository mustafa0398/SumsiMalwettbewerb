package com.example.sumsimalwettbewerb.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sumsimalwettbewerb.R
import com.example.sumsimalwettbewerb.fragments.InfoDialogFragment


class PrivacyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val privacyTv = view.findViewById<TextView>(R.id.privacyTv)
        privacyTv.setOnClickListener {
            showPrivacyDetails()
        }
    }
    private fun showPrivacyDetails() {

        val message = getString(R.string.info1)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(childFragmentManager, "InfoDialog")
    }
}
