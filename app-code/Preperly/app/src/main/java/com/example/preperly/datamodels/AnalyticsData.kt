package com.example.preperly.datamodels

// Data classes
data class Transaction(
    val name: String,
    val amount: Double,
    val date: String
)

data class RevenueData(
    val day: String,
    val amount: Float
)

// Add TimeFilter enum
enum class TimeFilter {
    DAILY,
    WEEKLY,
    MONTHLY
}