package com.example.coursesmanager.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Sinh viên có thể thay thế bằng đường dẫn MockAPI cá nhân của mình
    // Ví dụ: "https://65123456789abcdef.mockapi.io/api/v1/"
    @Volatile
    var customBaseUrl: String = "https://6602521c9b451f6f27715b74.mockapi.io/api/v1/"

    private var retrofit: Retrofit? = null

    private fun getOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun getApiService(baseUrl: String = customBaseUrl): MockApiService {
        val ensuredBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val client = getOkHttpClient()
        
        val retrofitInstance = Retrofit.Builder()
            .baseUrl(ensuredBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit = retrofitInstance
        return retrofitInstance.create(MockApiService::class.java)
    }
}
