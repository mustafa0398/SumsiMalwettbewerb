package com.example.sumsimalwettbewerb

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sumsimalwettbewerb.fragments.AgbFragment
import com.example.sumsimalwettbewerb.fragments.DisclaimerFragment
import com.example.sumsimalwettbewerb.fragments.ImprintFragment
import com.example.sumsimalwettbewerb.fragments.InfoDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), InfoDialogFragment.InfoDialogListener {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigationView.setupWithNavController(navController)

        val storedToken = RetrofitClient.getStoredAuthToken(this)
        if (storedToken.isEmpty()) {
            getTokenAndSave()
        }
    }

    private fun getTokenAndSave() {
        RetrofitClient.getToken(this) { token ->
            if (token.isNotEmpty()) {
            } else {
                Log.e("MainActivity", "Token konnte nicht abgerufen werden")
            }
        }
    }

    fun showPrivacyDetails(view: View) {

        val message = getString(R.string.showPrivacyDetails)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(supportFragmentManager, "InfoDialog")
    }

    fun showTermsAndConditions(view: View) {

        val message = getString(R.string.showTermsAndCondition)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(supportFragmentManager, "InfoDialog")
    }

    fun showCookiePreferences(view: View) {

        val message = getString(R.string.showCookiesPreferences)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(supportFragmentManager, "InfoDialog")
    }
    fun showAGB(view: View) {
        val message = getString(R.string.showAGB)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.listener = this
        dialogFragment.show(supportFragmentManager, "AGBDialog")
    }

    fun showImprint(view: View) {
        val message = getString(R.string.showImprint)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.listener = this
        dialogFragment.show(supportFragmentManager, "ImprintDialog")
    }

    fun showDisclaimer(view: View) {
        val message = getString(R.string.showDisclaimer)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.listener = this
        dialogFragment.show(supportFragmentManager, "DisclaimerDialog")
    }
    override fun onDialogPositiveClick(tag: String) {
        when (tag) {
            "ImprintDialog" -> {
                val imprintFragment = ImprintFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, imprintFragment)
                    .addToBackStack(null)
                    .commit()
            }
            "AGBDialog" -> {
                val agbFragment = AgbFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, agbFragment)
                    .addToBackStack(null)
                    .commit()
            }
            "DisclaimerDialog" -> {
                val disclaimerFragment = DisclaimerFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, disclaimerFragment)
                    .addToBackStack(null)
                    .commit()
            }
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (fragment) {
            is DisclaimerFragment -> {
                if (fragment.canWebViewGoBack()) {
                    fragment.goBackInWebView()
                    return
                }
            }
            is AgbFragment -> {
                if (fragment.canWebViewGoBack()) {
                    fragment.goBackInWebView()
                    return
                }
            }
            is ImprintFragment -> {
                if (fragment.canWebViewGoBack()) {
                    fragment.goBackInWebView()
                    return
                }
            }
        }
        super.onBackPressed()
    }
}