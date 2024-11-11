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
