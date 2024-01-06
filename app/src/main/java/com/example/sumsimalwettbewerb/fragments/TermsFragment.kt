package com.example.sumsimalwettbewerb.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sumsimalwettbewerb.R
import com.example.sumsimalwettbewerb.fragments.InfoDialogFragment

class TermsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val TermsTv = view.findViewById<TextView>(R.id.termsTv)
        TermsTv.setOnClickListener {
            showTermsAndConditions()
        }
    }
    private fun showTermsAndConditions() {

        val message = getString(R.string.info2)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(childFragmentManager, "InfoDialog")
    }
}
