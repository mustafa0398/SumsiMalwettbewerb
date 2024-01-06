package com.example.sumsimalwettbewerb.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumsimalwettbewerb.ApiResult
import com.example.sumsimalwettbewerb.ApiService
import com.example.sumsimalwettbewerb.PhotoAdapter
import com.example.sumsimalwettbewerb.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        loadSubmissions()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(requireContext(), photos)
        photoRecyclerView.layoutManager = LinearLayoutManager(context)
        photoRecyclerView.adapter = photoAdapter
    }

    private fun loadPhotos() {
        val photoNames = listOf("kleiner", "malerei", "shutterstock", "malwettbewerb")
        photos.addAll(photoNames)
        photoAdapter.notifyDataSetChanged()
    }

    private fun loadSubmissions() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getAllSubmissions("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiOGMxY2RmMTJlMGExMzJkYmYxNGZkZDQ0NjI1ZTA3ODFmZjA5MzkwNjZkODU1YzMyZmZhY2FhZDIxODU3ZDM0NTBjNThkZWQ4MmEyNDM2N2EiLCJpYXQiOjE3MDM2MTAwNzQuNzM0Mzk2LCJuYmYiOjE3MDM2MTAwNzQuNzM0Mzk5LCJleHAiOjE3MzUyMzI0NzQuNzI3MzEzLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.iJ8zGPtlc78E7c15hTFXpwyk8XTj5qcNcEo08H88lsZ8jjeO3f5W0ux4tEYhPOJ5bhkuzKBfTgAjf2fS0vu39rqde-wWiCFcZ-B778RnFxAmFgVf_6FICw33_G4lCUdiTPjRBBRQWtPV78X5KvJMS3SIcPwW6TC9suJF5D49Zu8OtOPENoH4Off_pWTF3p0n8czoLjIf0V4fcFW8fivdq5qhEVHSflW7H5x-3DFnjnueN81CTXgHs2ZlVzgvkhxxlpgz7cNduzj9oCCvMvdNUXI2PWYMzyL8kUwf8BJNEs47EWg2BrVS4bi_rbBzIHDwPGbAy6nsql6QxtzcJW6QgK-tzK7UHjEPNPrAH5Hpj1Ow--IZ4votVFkT8PW2BwDsGBlqmHqIpONb4O7hCMae7zB2TIgLXvCglDr5AzVNmFN9K1XGM-uHBw1CCWmoYc_XfyLi3fcOwEnzGAsfAyv5XbRgHoYg4djbUO7ZiFuM9ixU7HyJoWhBU9PadHm9YEnqLeWOr3BC_VrSLtG6-eerre20WEBZYPMAoSL8psCFhDrySn8eTARKvY2PagKNsYTJCYm9Y3pt2WVlpwA-yLC5rUbOqI9CJLjtjBZj5v6wMjPyyb0aXXPPPHIfLoJGRR_9GBiTgyCDeGaf8TO5yK1OspgCY1YAUQVh_DEr3eIzgWc")

        call.enqueue(object : Callback<ApiResult> {
            override fun onResponse(call: Call<ApiResult>, response: Response<ApiResult>) {
                if (response.isSuccessful) {
                    val submissions = response.body()?.data ?: emptyList()
                    photos.clear()
                    submissions.forEach { submission ->
                        submission.image?.public_location?.let { imageUrl ->
                            Log.d("PhotoFragment", "Image URL: $imageUrl")
                            photos.add(imageUrl)
                        }
                    }
                    photoAdapter.notifyDataSetChanged()
                    if (photos.isEmpty()) {
                        Log.d("PhotoFragment", "Keine Bilder zum Anzeigen")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("PhotoFragment", "API call failed: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ApiResult>, t: Throwable) {
                Log.e("PhotoFragment", "API call failed: ${t.message}")
            }
        })
    }

    companion object {
        private const val READ_MEDIA_IMAGES_PERMISSION_CODE = 123
    }


}