package com.example.sumsimalwettbewerb.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumsimalwettbewerb.PhotoAdapter
import com.example.sumsimalwettbewerb.R

class PhotoFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private var photos: MutableList<String> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoRecyclerView = view.findViewById(R.id.recycler_view)
        setupRecyclerView()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), READ_MEDIA_IMAGES_PERMISSION_CODE)
        } else {
            loadPhotos()
        }
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(requireContext(), photos)
        photoRecyclerView.layoutManager = LinearLayoutManager(context)
        photoRecyclerView.adapter = photoAdapter
    }

    private fun loadPhotos() {
        // Hier sollten Sie den Code zum Laden von Fotos aus Ihrem Speicher hinzufügen
        // Beispiel für das Hinzufügen statischer Fotopfade
        val imagePath = "file:///storage/emulated/0/Download/1.jpg"
        val imagePath2 = "file:///storage/emulated/0/Download/2.jpg"
        val imagePath3 = "file:///storage/emulated/0/Download/3.jpg"
        val imagePath4 = "file:///storage/emulated/0/Download/4.jpg"
        val imagePath5 = "file:///storage/emulated/0/Download/5.jpg"

        photos.add(imagePath)
        photos.add(imagePath2)
        photos.add(imagePath3)
        photos.add(imagePath4)
        photos.add(imagePath5)


        photoAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val READ_MEDIA_IMAGES_PERMISSION_CODE = 123
    }
}