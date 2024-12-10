package com.example.preperly.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RestaurantDashboardViewModel : ViewModel(){

    var isOnline by mutableStateOf(true)
    var hasNotifications by mutableStateOf(false)

    fun toggleOnlineStatus() {
        isOnline = !isOnline
    }
}