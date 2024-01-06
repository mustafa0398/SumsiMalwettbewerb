package com.example.sumsimalwettbewerb

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("api/v1/submissions")
    fun uploadImage(
        @Header("Authorization") authHeader: String,
        @Part image: MultipartBody.Part,
        @Part legalGuardianFirstName: MultipartBody.Part,
        @Part legalGuardianLastName: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part childFirstName: MultipartBody.Part,
        @Part childAge: MultipartBody.Part,
        @Part approvalPrivacyPolicy: MultipartBody.Part,
        @Part approvalParticipation: MultipartBody.Part,
        @Part approvalMailNotification: MultipartBody.Part
    ): Call<SubmissionResponse>

    @GET("api/v1/submissions")
    fun getAllSubmissions(
        @Header("Authorization") authHeader: String
    ): Call<ApiResult>

}