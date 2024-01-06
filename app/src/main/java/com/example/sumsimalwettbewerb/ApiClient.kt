package com.example.sumsimalwettbewerb

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://sumsi.dev.webundsoehne.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer {eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiOTRjNjhkY2MzYWZjNWY2MmJiYzk3NTg2YzI0MmU5YWFmNWU3NWQxODY2Mjk4ZmY4ZmUzNDU4OWI4YzE1NDhlMDUxMmRkOWRiYzEzYjc4N2IiLCJpYXQiOjE3MDQ0MDA5NzIuODM1OTUxLCJuYmYiOjE3MDQ0MDA5NzIuODM1OTU0LCJleHAiOjE3MzYwMjMzNzIuODI4MzQ4LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.ecgSLnDUUL0U_VRvO5dO39fv0anLgWpwlv4hMGFI0oQZSsiuSSAnbWpmp1CB_6rZ4koA1niYrKKfJbgz08Lo0gk82UCwW1QaL78ORS78ehPk6kxMCgLE7K9cO8v08sbdr792TfaZIPW0VU_YxQ3Sp03-jmRphL-col1k98Y253Ea2KPzDZ5lSKNMizXOWD_GnQvpYj9Y2h4DSXmOYWBrbRZ54V5wtwZpVYxLvcpmv2Sx_Nt47pQKF-BiCaZGoz5Hkl6G1YQrrOWfqApRvw2pJ4PqRAOF4aasA7llUGUKv0Zwv_kXBW0PXR3IorwtTdu9dkti-20GJOWcqwPvAXA_Jtw2G8flBcTjN5anpB36oQzZmg6ZYsSu7lUb29V3wH740M97U7U633nZSpNCh1TBCSHYNtKbfATCIul5msjBQQVL32EHtWDU0RS8ENJcFWpskX2s-c9UkMbpBokwgbKEKYkeU8jW0Awxo3KbNzG-4PlElYh3YTmPBRXeD3fgHzoPvTFsx8AWlwrsJFKQIZmmgZK_bYcn8vTrH_dl4gCW8SLFtAgQPBaNI0_i-RNCnThO3M-SqIlhe8SFSVkaHn3HvUvXzSIz_M3Afeu7uoai85hBcvIgerzMSZI_tKcJF0Ru5vyJ4Myv_6fS0LaCTP8xv_iyuA-Lqe791OV-3KXZ22s}")
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

}
