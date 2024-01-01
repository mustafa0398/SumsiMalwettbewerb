package com.example.sumsimalwettbewerb.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.sumsimalwettbewerb.R

class AgbFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        val webView: WebView = view.findViewById(R.id.webView)
        val url = "https://www.raiffeisen.at/ktn/rlb/de/meine-bank/raiffeisen-bankengruppe/agb.html"
        webView.loadUrl(url)
    }
}
