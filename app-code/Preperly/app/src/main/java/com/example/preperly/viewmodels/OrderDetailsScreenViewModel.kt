package com.example.preperly.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.preperly.datamodels.Order
import com.example.preperly.datamodels.OrderStatus


class OrderDetailsScreenViewModel : ViewModel(){

    var orderdetails by mutableStateOf<List<Order>>(emptyList())

    fun intializeOrderDetails(){

        orderdetails = listOf(
            Order(
                orderId = "111",
                numberOfPeople = 3,
                time = "5:30 PM",
                totalItems = 7,
                amount = 500.76,
                status = OrderStatus.ACTIVE,
                itemsList = emptyList()
            ),
            Order(
                orderId = "112",
                numberOfPeople = 5,
                time = "7:30 PM",
                totalItems = 10,
                amount = 1507.78,
                status = OrderStatus.PENDING,
                itemsList = emptyList()
            )
        )
    }
}