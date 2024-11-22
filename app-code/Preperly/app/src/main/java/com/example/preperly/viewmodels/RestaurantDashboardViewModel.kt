package com.example.preperly.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.preperly.screens.MenuItem


// ViewModel
class RestaurantDashboardViewModel : ViewModel() {
    var isOnline by mutableStateOf(true)
    var menuItems by mutableStateOf<List<MenuItem>>(emptyList())
    var hasNotifications by mutableStateOf(false)

    fun initialItems(){
        menuItems = listOf(
            MenuItem(
                id = "1",
                name = "Margherita Pizza",
                description = "Classic tomato and mozzarella",
                imageUrl = "https://example.com/pizza.jpg",
                isAvailable = true
            ),
            MenuItem(
                id = "2",
                name = "Caesar Salad",
                description = "Romaine lettuce with Caesar dressing",
                imageUrl = "https://example.com/salad.jpg",
                isAvailable = false
            )
        )
    }

    fun toggleAvailability(itemId: String) {
        menuItems = menuItems.map {
            if (it.id == itemId) it.copy(isAvailable = !it.isAvailable)
            else it
        }
    }

    fun toggleOnlineStatus() {
        isOnline = !isOnline
    }
}