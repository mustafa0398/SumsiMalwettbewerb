package com.example.sumsimalwettbewerb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.sumsimalwettbewerb.R


class InfoDialogFragment : DialogFragment() {

    interface InfoDialogListener {
        fun onDialogPositiveClick(tag: String)
    }

    var listener: InfoDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val message = arguments?.getString(MESSAGE_KEY)

        builder.setMessage(Html.fromHtml(message)).setPositiveButton("OK") { dialog, _ ->
            tag?.let { listener?.onDialogPositiveClick(it) }
        }

        return builder.create()
    }

    companion object {
        private const val MESSAGE_KEY = "message"

        fun newInstance(message: String): InfoDialogFragment {
            val fragment = InfoDialogFragment()
            val args = Bundle()
            args.putString(MESSAGE_KEY, message)
            fragment.arguments = args
            return fragment
        }
    }
}
