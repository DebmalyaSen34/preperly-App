package com.example.preperly.datamodels

data class UserResponse(
    val success: Boolean,
    val message: String,
    val status: Int
)

data class SendOTPResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)

data class VerifyOTPResponse(
    val success: Boolean,
    val message: String
)