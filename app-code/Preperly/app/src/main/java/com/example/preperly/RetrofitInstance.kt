package com.example.preperly

import com.example.preperly.retrofitapi.OTPApiService
import com.example.preperly.retrofitapi.UserRegisterApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL =
        "https://preperly-apis.vercel.app/" // Change to your API's base URL

    private val okHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(false) // Ensure no automatic retries
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
    val userRegisterApi: UserRegisterApiService by lazy {
        retrofit.create(UserRegisterApiService::class.java)
    }

    val otpApi: OTPApiService by lazy {
        retrofit.create(OTPApiService::class.java)
    }
}
