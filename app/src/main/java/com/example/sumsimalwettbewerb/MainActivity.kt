package com.example.sumsimalwettbewerb

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sumsimalwettbewerb.Fragments.InfoDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

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
    fun showImprint(view: View) {
        val message = getString(R.string.showImprint)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(supportFragmentManager, "InfoDialog")
    }
    fun showAGB(view: View) {
        val message = getString(R.string.showAGB)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(supportFragmentManager, "InfoDialog")
    }
    fun showDisclaimer(view: View) {
        val message = getString(R.string.showDisclaimer)
        val dialogFragment = InfoDialogFragment.newInstance(message)
        dialogFragment.show(supportFragmentManager, "InfoDialog")

    }

}