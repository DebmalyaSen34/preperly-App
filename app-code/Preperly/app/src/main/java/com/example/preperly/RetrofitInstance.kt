package com.example.preperly

import com.example.preperly.retrofitapi.UserRegisterApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://preperly-apis.vercel.app/" // Change to your API's base URL

    val userRegisterApi: UserRegisterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserRegisterApiService::class.java)
    }
}
