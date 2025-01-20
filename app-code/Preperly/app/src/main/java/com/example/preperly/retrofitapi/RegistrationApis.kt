package com.example.preperly.retrofitapi

import com.example.preperly.datamodels.OTPRequest
import com.example.preperly.datamodels.SendOTPResponse
import com.example.preperly.datamodels.User
import com.example.preperly.datamodels.UserResponse
import com.example.preperly.datamodels.VerifyOTPResponse
import com.example.preperly.datamodels.VerifyRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserRegisterApiService {
    @POST("/api/user/register")
    fun createUser(@Body user: User): Call<UserResponse>
}

interface OTPApiService{
    @POST("/api/user/verification/sendOtp")
    fun sendOtp(@Body otpRequest: OTPRequest): Call<SendOTPResponse>

    @POST("/api/user/verification/verifyOtp")
    fun verifyOtp(@Body verifyRequest: VerifyRequest): Call<VerifyOTPResponse>
}

