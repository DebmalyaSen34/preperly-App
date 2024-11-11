package com.example.preperly.retrofitapi

import com.example.preperly.datamodels.User
import com.example.preperly.datamodels.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserRegisterApiService {
    @POST("/api/user/register")
    fun createUser(@Body user: User): Call<UserResponse>
}
