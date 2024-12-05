package com.example.preperly.datamodels

data class User(
    val restaurantName: String,
    val restaurantAddress: String,
    val phoneNumber: String,
    val alternateNumber: String,
    val email: String,
    val password: String,
    val ownerName: String,
    val ownerPhoneNumber: String,
    val ownerEmail: String,
    val receiveUpdatesOnWhatsApp: Boolean
)

data class OTPRequest(val phoneNumber: String)

data class VerifyRequest(val phoneNumber: String, val otp: String)

data class TimeSlot(var openTime: String, var closeTime: String)

data class DayTimeSlots(
    val day: String,
    val slots: List<TimeSlot>
)
