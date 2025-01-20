package com.example.preperly.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private var phoneNumber = mutableStateOf("")

    fun updatePhoneNumber(number: String){
        phoneNumber.value = number
    }

    fun getPhoneNumber(): String{
        return phoneNumber.value
    }
}