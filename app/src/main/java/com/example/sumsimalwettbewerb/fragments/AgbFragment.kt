package com.example.sumsimalwettbewerb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.sumsimalwettbewerb.R

class AgbFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://www.raiffeisen.at/ktn/rlb/de/meine-bank/raiffeisen-bankengruppe/agb.html")
    }

    fun canWebViewGoBack(): Boolean {
        return webView.canGoBack()
    }

    fun goBackInWebView() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
}
