package com.example.sumsimalwettbewerb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.sumsimalwettbewerb.R


class InfoDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val message = arguments?.getString(MESSAGE_KEY)

        builder.setMessage(Html.fromHtml(message)).setPositiveButton("OK") {
            dialog,_ -> dialog.dismiss()
        }
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.textViewLink)?.setOnClickListener {
           
            val webViewFragment = ImprintFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,webViewFragment)
                .addToBackStack(null)
                .commit()
                dismiss()
        }
    }
    companion object {
        private const val MESSAGE_KEY = "message"

        fun newInstance (message:String): InfoDialogFragment {
            val fragment = InfoDialogFragment()
            val args = Bundle()
            args.putString(MESSAGE_KEY,message)
            fragment.arguments =args
            return fragment
        }
    }
}