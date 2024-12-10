package com.example.preperly.datamodels

data class OrderItem(
    val quantity: Int,
    val name: String,
    val price: Double
)

data class Order(
    val orderId: String,
    val numberOfPeople: Int,
    val time: String,
    val totalItems: Int,
    val amount: Double,
    val status: OrderStatus,
    val itemsList: List<OrderItem>
)

enum class OrderStatus {
    COMPLETED, ACTIVE, PENDING
}