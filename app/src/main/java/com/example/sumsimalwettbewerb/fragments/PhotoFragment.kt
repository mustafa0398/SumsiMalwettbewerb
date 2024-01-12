package com.example.sumsimalwettbewerb.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumsimalwettbewerb.ApiResult
import com.example.sumsimalwettbewerb.ApiService
import com.example.sumsimalwettbewerb.Photo
import com.example.sumsimalwettbewerb.PhotoAdapter
import com.example.sumsimalwettbewerb.R
import com.example.sumsimalwettbewerb.Submission
import com.example.sumsimalwettbewerb.SumsiDao
import com.example.sumsimalwettbewerb.SumsiData
import com.example.sumsimalwettbewerb.SumsiDatabase
import com.example.sumsimalwettbewerb.Vote
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class PhotoFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private var photos: MutableList<Photo> = mutableListOf()
    private val executorService = Executors.newFixedThreadPool(4)

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
        val database = SumsiDatabase.getDatabase(requireContext())
        val sumsiDao = database.sumsiDao()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        service.getAllSubmissions("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSU" +
                "zI1NiJ9.eyJhdWQi" + "OiIxIiwianRpIjoiYTExNjYwNDE0ZTJjM2ExODBmZGN" +
                "mOGIxZTExYzcwMjlkMThkOTA2ZmYxZjZiNGU1YjUwYzJhMTNjZDA1ZTQ4ZmZlZTM3ZGZkYWYy" +
                "ZjQ3N2IiLCJpYXQiOjE3MDUwMDk5NzEuNDAwNDY4LCJuYmYiOjE3MDUwM" +
                "Dk5NzEuNDAwNDc1LCJleHAiOjE3MzY2MzIzNzEuMzkwMjY1LCJzdWIiOiI" +
                "xIiwic2NvcGVzIjpbXX0.NvUJHZ_XC6Lj1M0cWUBvPu9ahG5QR_d-wvoM" +
                "vYXoqjUl8rO6kccbMifthMQA5OuXT1l_8A0EwDkM8LqJTaOdMKM9UNZiy5" +
                "iYFKGm97yksJXYmyk0g2xRjJ6tG2JBqJCL0y3dLs8yj9Ba7rWsOdfSTcJ" +
                "22WE4wmjp9nt9QKHY3paIeV97u5F9FsrIOms2gjQfu2XGk1vrKkHSjnNhbWu" +
                "4Xnmp77lfbYWzOYavVKLWRwByeVOHiSz6o4rW9QazqmG9B2DVbR4NIAc0Euj" +
                "VrAGJ56o5o_NHuk2kGdYXqvR7oexyEILGEFhsF4qGwBNvofahXgLiOAob-rF" +
                "MgUKUxy5Vz-tz5cHsoVxCrljUu8mYl8kwUwacql2YKUto_K7iH5cufFULWBLXQ" +
                "vbZUH8Tw_c-VbguPBK4ZfFDK78Kg4c7VEl7-ICQPCnrGFWX49DjTrSuCq4L_6H" +
                "xY4s-EaM2EcftysebVN4UEZPA49xzyRD8sH71TLFXoliyrghKfo8h7YOU4GJ5" +
                "yoWvR9htc8ZwkhjcMWpQiWUasuQHFr_KPF8fKXI2Gqcr8oymv-363iJaVHJ" +
                "v5KYBoHc7bB5e5ED4ZleVpkdnpB5tBbNyutsnhfrH13r8_AnrMk03O30hGJX" +
                "EsZBJRHBd_JuwGiQTR2KnaS8uCpMeRcj5cmZeK6" +
                "XKrgyNf9k").enqueue(object : Callback<ApiResult> {
            override fun onResponse(call: Call<ApiResult>, response: Response<ApiResult>) {
                if (response.isSuccessful) {
                    val submissions = response.body()?.data ?: emptyList()

                    submissions.forEach { submission ->
                        val imageUrl = "https://sumsi.dev.webundsoehne.com" + submission.image?.public_location
                        downloadAndSaveImageAsync(imageUrl) { localImagePath ->
                            val sumsiData = SumsiData(
                                id = submission.id,
                                legalguardian_firstname = submission.legalguardian_firstname,
                                legalguardian_lastname = submission.legalguardian_lastname,
                                email = submission.email,
                                child_firstname = submission.child_firstname,
                                child_age = submission.child_age,
                                approval_privacypolicy = submission.approval_privacypolicy,
                                approval_participation = submission.approval_participation,
                                approval_mailnotification = submission.approval_mailnotification,
                                created_at = submission.created_at,
                                updated_at = submission.updated_at,
                                imageId = submission.image?.id ?: 0,
                                votingCount = submission.votings.size,
                                localImagePath = localImagePath
                            )


                            executorService.execute {
                                sumsiDao.insertSumsiData(sumsiData)
                            }
                        }
                    }

                    updateUIWithSubmissions(submissions)
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("PhotoFragment", "API-Aufruf fehlgeschlagen: $errorMessage")
                    loadLocalData(sumsiDao)
                }
            }

            override fun onFailure(call: Call<ApiResult>, t: Throwable) {
                Log.e("PhotoFragment", "API-Aufruf fehlgeschlagen: ${t.message}")
                loadLocalData(sumsiDao)
            }
        })
    }

    private fun downloadAndSaveImageAsync(imageUrl: String?, callback: (String?) -> Unit) {
        Thread {
            if (isAdded && context != null) {
                val imagePath = downloadAndSaveImage(imageUrl)

                Handler(Looper.getMainLooper()).post {
                    callback(imagePath)
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }.start()
    }



    private fun downloadAndSaveImage(imageUrl: String?): String? {
        if (imageUrl == null) return null

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(80, TimeUnit.SECONDS)
            .readTimeout(80, TimeUnit.SECONDS)
            .writeTimeout(80, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder().url(imageUrl).build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Fehler bei der Serverantwort")

                val fileName = "image_${System.currentTimeMillis()}.jpg"
                val file = File(requireContext().filesDir, fileName)

                response.body?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                return file.absolutePath
            }
        } catch (e: IOException) {
            Log.e("DownloadImage", "Fehler beim Herunterladen des Bildes: ${e.message}")
        }

        return null
    }

    private fun updateUIWithSubmissions(submissions: List<Submission>) {
        photos.clear()
        val remainingSubmissions = submissions.toMutableList()

        fun processSubmissionAsync(submission: Submission) {
            val photoVotes = submission.votings.map { convertToVote(it) }.toMutableList()

            val childName = submission.child_firstname
            val childAge = submission.child_age

            getLocalImagePath(submission.image?.id) { localImagePath ->
                val photo = Photo(
                    id = submission.id,
                    imageUrl = "https://sumsi.dev.webundsoehne.com" + (submission.image?.public_location ?: ""),
                    localImagePath = localImagePath,
                    voteCount = submission.votings.size,
                    votes = photoVotes,
                    hasVoted = false,
                    childName = childName,
                    childAge = childAge
                )
                photos.add(photo)

                remainingSubmissions.remove(submission)
                if (remainingSubmissions.isEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        photoAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        submissions.forEach { submission ->
            processSubmissionAsync(submission)
        }
    }

    private fun getLocalImagePath(imageId: Int?, callback: (String?) -> Unit) {
        val contextWeakRef = WeakReference(requireContext())

        executorService.execute {
            val context = contextWeakRef.get()
            if (context != null && imageId != null) {
                val localImagePath = SumsiDatabase.getDatabase(context).sumsiDao().getSumsiDataById(imageId)?.localImagePath

                Handler(Looper.getMainLooper()).post {
                    callback(localImagePath)
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    private fun loadLocalData(sumsiDao: SumsiDao) {
        sumsiDao.getAllSumsiData().observe(viewLifecycleOwner, Observer { localData ->
            val localPhotos = localData.map { sumsiData ->
                Photo(
                    id = sumsiData.id,
                    imageUrl = sumsiData.localImagePath ?: "",
                    localImagePath = sumsiData.localImagePath,
                    voteCount = sumsiData.votingCount,
                    votes = mutableListOf(),
                    hasVoted = false,
                    childName = sumsiData.child_firstname,
                    childAge = sumsiData.child_age

                )
            }
            updateAdapter(localPhotos)
        })
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

    private fun updateAdapter(localPhotos: List<Photo>) {
        photoAdapter.updateData(localPhotos)
    }

}
