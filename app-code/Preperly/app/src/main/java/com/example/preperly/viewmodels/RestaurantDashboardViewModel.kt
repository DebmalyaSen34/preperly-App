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