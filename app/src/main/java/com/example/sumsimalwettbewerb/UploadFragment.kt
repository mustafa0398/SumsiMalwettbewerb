package com.example.sumsimalwettbewerb

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient

class UploadFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.upload_images, container, false)

        webView = view.findViewById(R.id.information_contest)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        webView.setBackgroundColor(Color.parseColor("#FBF315"))

        val css = resources.getString(R.string.custom_css)
        val htmlString = getString(R.string.information_contest)
        val html = """<html><head><style type="text/css">$css</style></head><body>$htmlString</body></html>"""
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

        return view
    }
}