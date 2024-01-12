package com.example.sumsimalwettbewerb

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://sumsi.dev.webundsoehne.com/"

    private val okHttpClient = OkHttpClient.Builder().build()

    fun getToken(context: Context, callback: (String) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(ApiService::class.java)
        val loginData = LoginData(Constants.DEFAULT_EMAIL, Constants.DEFAULT_PASSWORD)

        service.login(loginData).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token.orEmpty()
                    saveAuthToken(context, token)
                    callback(token)
                } else {
                    Log.e("RetrofitClient", "Login failed: ${response.errorBody()?.string()}")
                    callback("")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("RetrofitClient", "Network error: ${t.message}")
                callback("")
            }
        })
    }

    private fun saveAuthToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("AuthToken", token).apply()
    }

    fun getStoredAuthToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("AuthToken", "") ?: ""
    }
}
