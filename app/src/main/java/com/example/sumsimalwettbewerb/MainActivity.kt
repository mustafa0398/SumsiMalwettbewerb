package com.example.sumsimalwettbewerb

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.sumsimalwettbewerb.fragments.InfoDialogFragment
import com.example.sumsimalwettbewerb.fragments.PhotoAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var photos: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //RecyclerViewPhotoAdapter
        photoRecyclerView = findViewById(R.id.recycler_view)
        photos = mutableListOf()

        // Request permission to read external storage
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        } else {
            loadPhotos()
        }
    }

    // Load photos from the gallery
    private fun loadPhotos() {
photos.add("content://media/external/images/media/1")
        photos.add("content://media/external/images/media/2")
        photos.add("content://media/external/images/media/3")
        photos.add("content://media/external/images/media/4")
        photos.add("content://media/external/images/media/5")

        // Initialize and set up the RecyclerView

        photoAdapter = PhotoAdapter(this, photos)
        photoRecyclerView.layoutManager = LinearLayoutManager(this)
        photoRecyclerView.adapter = photoAdapter
    }

    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 123
    }

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



