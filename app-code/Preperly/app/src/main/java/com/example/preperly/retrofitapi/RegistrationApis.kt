package com.example.preperly.retrofitapi

import com.example.preperly.datamodels.DayTimeSlots
import com.example.preperly.datamodels.MenuItem
import com.example.preperly.datamodels.OTPRequest
import com.example.preperly.datamodels.SendOTPResponse
import com.example.preperly.datamodels.User
import com.example.preperly.datamodels.UserResponse
import com.example.preperly.datamodels.VerifyOTPResponse
import com.example.preperly.datamodels.VerifyRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserRegisterApiService {
    @POST("/api/user/register")
    fun createUser(@Body user: User): Call<UserResponse>

    @POST("/api/user/resType")
    fun restaurantTypeDetails(@Body phoneNumber: String,resTimings: List<DayTimeSlots>, resCuisine: String): Call<UserResponse>

    @Multipart
    @POST("/api/user/docData")
    fun restaurantDocData(
        @Body phoneNumber: String,
        @Part("fssaiLicence") fssaiLicence: RequestBody,
        @Part("gstin") gstin: RequestBody,
        @Part("panCard") panCard: RequestBody,
        @Part("accountHolderName") accountHolderName: RequestBody,
        @Part("accountNumber") accountNumber: RequestBody,
        @Part fssaiDocument: MultipartBody.Part,
        @Part gstinDocument: MultipartBody.Part,
        @Part panCardDocument: MultipartBody.Part
    ): Call<UserResponse>

    @Multipart
    @POST("api/user/uploadImages")
    fun uploadImages(
        @Body phoneNumber: String,
        @Part restaurantLogos: List<MultipartBody.Part>,
        @Part restaurantImages: List<MultipartBody.Part>
    ): Call<UserResponse>


    @POST("api/user/addMenu")
    fun addMenu(@Body phoneNumber: String,menuItems: List<MenuItem>): Call<UserResponse>
}

interface OTPApiService{
    @POST("/api/user/verification/sendOtp")
    fun sendOtp(@Body otpRequest: OTPRequest): Call<SendOTPResponse>

    @POST("/api/user/verification/verifyOtp")
    fun verifyOtp(@Body verifyRequest: VerifyRequest): Call<VerifyOTPResponse>
}