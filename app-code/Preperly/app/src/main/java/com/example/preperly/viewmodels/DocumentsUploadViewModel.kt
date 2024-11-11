package com.example.preperly.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DocumentsUploadViewModel {

    var currentStep by mutableIntStateOf(3)

    var fssaiLicence by mutableStateOf("")
        private set

    var gstin by mutableStateOf("")
        private set

    var panCard by mutableStateOf("")
        private set

    var accountHolderName by mutableStateOf("")
        private set

    var accountNumber by mutableStateOf("")
        private set

    fun updateFssaiLicence(newValue: String) {
        fssaiLicence = newValue
    }
    fun updateGstin(newValue: String){
        gstin = newValue
    }
    fun updatePanCard(newValue: String){
        panCard = newValue
    }
    fun updateAccountHolderName(newValue: String){
        accountHolderName = newValue
    }
    fun updateAccountNumber(newValue: String){
        accountNumber = newValue
    }

}

