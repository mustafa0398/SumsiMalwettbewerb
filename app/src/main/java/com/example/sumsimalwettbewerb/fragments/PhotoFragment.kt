package com.example.sumsimalwettbewerb.fragments

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
import com.example.sumsimalwettbewerb.Photo
import com.example.sumsimalwettbewerb.PhotoAdapter
import com.example.sumsimalwettbewerb.R
import com.example.sumsimalwettbewerb.Submission
import com.example.sumsimalwettbewerb.Vote
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class PhotoFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private var photos: MutableList<Photo> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoRecyclerView = view.findViewById(R.id.recycler_view)
        setupRecyclerView()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), READ_MEDIA_IMAGES_PERMISSION_CODE)
        }
        loadSubmissions()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(requireContext(), photos)
        photoRecyclerView.layoutManager = LinearLayoutManager(context)
        photoRecyclerView.adapter = photoAdapter
    }


    private fun loadSubmissions() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)


        service.getAllSubmissions("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9." +
                "eyJhdWQiOiIxIiwianRpIjoiOGMxY2RmMTJlMGExMzJkYmYxNGZkZDQ0N" +
                "jI1ZTA3ODFmZjA5MzkwNjZkODU1YzMyZmZhY2FhZDIxODU3ZDM0NTBjN" +
                "ThkZWQ4MmEyNDM2N2EiLCJpYXQiOjE3MDM2MTAwNzQuNzM0Mzk2LCJuYm" +
                "YiOjE3MDM2MTAwNzQuNzM0Mzk5LCJleHAiOjE3MzUyMzI0NzQuNzI3M" +
                "zEzLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.iJ8zGPtlc78E7c15hTFXp" +
                "wyk8XTj5qcNcEo08H88lsZ8jjeO3f5W0ux4tEYhPOJ5bhkuzKBfTgAjf2" +
                "fS0vu39rqde-wWiCFcZ-B778RnFxAmFgVf_6FICw33_G4lCUdiTPjRBB" +
                "RQWtPV78X5KvJMS3SIcPwW6TC9suJF5D49Zu8OtOPENoH4Off_pWTF3p0" +
                "n8czoLjIf0V4fcFW8fivdq5qhEVHSflW7H5x-3DFnjnueN81CTXgHs2Zl" +
                "Vzgvkhxxlpgz7cNduzj9oCCvMvdNUXI2PWYMzyL8kUwf8BJNEs47EWg2Br" +
                "VS4bi_rbBzIHDwPGbAy6nsql6QxtzcJW6QgK-tzK7UHjEPNPrAH5Hpj1" +
                "Ow--IZ4votVFkT8PW2BwDsGBlqmHqIpONb4O7hCMae7zB2TIgLXvCgl" +
                "Dr5AzVNmFN9K1XGM-uHBw1CCWmoYc_XfyLi3fcOwEnzGAsfAyv5XbRg" +
                "HoYg4djbUO7ZiFuM9ixU7HyJoWhBU9PadHm9YEnqLeWOr3BC_VrSLtG6-" +
                "eerre20WEBZYPMAoSL8psCFhDrySn8eTARKvY2PagKNsYTJCYm9Y3pt2W" +
                "VlpwA-yLC5rUbOqI9CJLjtjBZj5v6wMjPyyb0aXXPPPHIfLoJGRR_9GBi" +
                "TgyCDeGaf8TO5yK1OspgCY1YAUQVh_DEr3eIzgWc").enqueue(object : Callback<ApiResult> {
            override fun onResponse(call: Call<ApiResult>, response: Response<ApiResult>) {
                if (response.isSuccessful) {
                    val submissions = response.body()?.data ?: emptyList()
                    updateUIWithSubmissions(submissions)
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

    private fun updateUIWithSubmissions(submissions: List<Submission>) {
        photos.clear()
        submissions.forEach { submission ->
            val photoVotes = submission.votings.map { convertToVote(it) }.toMutableList()

            val photo = Photo(
                id = submission.id,
                imageUrl = "https://sumsi.dev.webundsoehne.com" + (submission.image?.public_location ?: ""),
                voteCount = submission.votings.size,
                votes = photoVotes,
                hasVoted = false
            )
            photos.add(photo)
        }
        photoAdapter.notifyDataSetChanged()
    }
    private fun convertToVote(voting: Submission.Voting): Vote {
        return Vote(
            id = voting.id,
            email = voting.email,
            submissionId = voting.submission_id,
            createdAt = voting.created_at ?: "",
            updatedAt = voting.updated_at ?: ""
        )
    }

    companion object {
        private const val READ_MEDIA_IMAGES_PERMISSION_CODE = 123
    }

}