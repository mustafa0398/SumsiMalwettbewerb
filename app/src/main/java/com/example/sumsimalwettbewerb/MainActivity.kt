package com.example.sumsimalwettbewerb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.sumsimalwettbewerb.fragments.InfoDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // NavigationBar
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNavView.setOnNavigationItemSelectedListener { item ->
            true
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
}



