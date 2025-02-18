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
    @POST("/api/auth/register?step=1")
    fun createUser(@Body user: User): Call<UserResponse>

    @POST("/api/auth/register/steps/step2")
    fun restaurantTypeDetails(@Body phoneNumber: String,resTimings: List<DayTimeSlots>, resCuisine: String): Call<UserResponse>

    @Multipart
    @POST("/api/auth/register/steps/step3")
    fun restaurantDocData(
        @Part("phoneNumber") phoneNumber: RequestBody,
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
    @POST("/api/auth/register/steps/step4")
    fun uploadImages(
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part restaurantLogos: List<MultipartBody.Part>,
        @Part restaurantImages: List<MultipartBody.Part>
    ): Call<UserResponse>


    @POST("/api/auth/register/steps/step5")
    fun addMenu(@Body phoneNumber: String,menuItems: List<MenuItem>): Call<UserResponse>
}

interface OTPApiService{
    @POST("/api/user/verification/sendOtp")
    fun sendOtp(@Body otpRequest: OTPRequest): Call<SendOTPResponse>

    @POST("/api/user/verification/verifyOtp")
    fun verifyOtp(@Body verifyRequest: VerifyRequest): Call<VerifyOTPResponse>
}