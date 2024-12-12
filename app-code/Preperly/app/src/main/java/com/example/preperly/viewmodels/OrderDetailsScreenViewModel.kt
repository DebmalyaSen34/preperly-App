package com.example.preperly.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.preperly.datamodels.Order
import com.example.preperly.datamodels.OrderItem
import com.example.preperly.datamodels.OrderStatus

class OrderDetailsScreenViewModel : ViewModel(){

    var orderdetails by mutableStateOf<List<Order>>(emptyList())

    fun initializeOrderDetails(){

        orderdetails = listOf(
            Order(
                orderId = "111",
                numberOfPeople = 3,
                time = "5:30 PM",
                totalItems = 7,
                amount = 1700.0,
                status = OrderStatus.ACTIVE,
                itemsList = listOf(
                    OrderItem(quantity = 3, name = "Margherita Pizza", price = 300.0),
                    OrderItem(quantity = 4, name = "Caesar Salad", price = 200.0)
                )
            ),
            Order(
                orderId = "112",
                numberOfPeople = 5,
                time = "7:30 PM",
                totalItems = 10,
                amount = 800.0,
                status = OrderStatus.PENDING,
                itemsList = listOf(
                    OrderItem(quantity = 2, name = "Margherita Pizza", price = 300.0),
                    OrderItem(quantity = 1, name = "Caesar Salad", price = 200.0)
                )
            )
        )
    }

    fun getOrderDetailsById(orderId : String) : Order? {
        return orderdetails.find { it.orderId == orderId }
    }
}