package com.example.sumsimalwettbewerb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.sumsimalwettbewerb.R

class DisclaimerFragment : Fragment() {

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
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        if (savedInstanceState == null) {
            val url = "https://www.raiffeisen.at/ktn/rlb/de/meine-bank/raiffeisen-bankengruppe/disclaimer.html"
            webView.loadUrl(url)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { webView.restoreState(it) }
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
