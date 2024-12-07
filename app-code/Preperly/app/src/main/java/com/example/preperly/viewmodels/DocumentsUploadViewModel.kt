package com.example.preperly.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DocumentsUploadViewModel {

    var fssaiDocument by mutableStateOf<Uri?>(null)

    var gstinDocument by mutableStateOf<Uri?>(null)

    var panCardDocument by mutableStateOf<Uri?>(null)

    var currentStep by mutableIntStateOf(3)

    var fssaiLicence by mutableStateOf("")
        private set
    var fssaiLicenceError by mutableStateOf("")
        private set

    var gstin by mutableStateOf("")
        private set
    var gstinError by mutableStateOf("")
        private set

    var panCard by mutableStateOf("")
        private set
    var panCardError by mutableStateOf("")
        private set

    var accountHolderName by mutableStateOf("")
        private set
    var accountNameError by mutableStateOf("")
        private set

    var accountNumber by mutableStateOf("")
        private set
    var accountNumberError by mutableStateOf("")
        private set

    var areAllDocumentsPresentError by mutableStateOf("")
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

    private fun isValidFSSAI(): Boolean {
        val pattern = Regex("^[2-9][0-9]{13}$")

        if(!pattern.matches(fssaiLicence)){
            fssaiLicenceError = "Invalid FSSAI Format"
            return false
        }
        fssaiLicenceError = ""
        return true
    }

    private fun isValidGSTIN(): Boolean {
        val pattern = Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][A-Z0-9]Z[A-Z0-9]$")

        if(!pattern.matches(gstin)){
            gstinError = "Invalid GSTIN format"
            return false
        }
        gstinError = ""
        return true
    }

    private fun isValidPAN(): Boolean {
        val pattern = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")

        if(!pattern.matches(panCard)){
            panCardError = "Invalid PAN format"
            return false
        }
        panCardError = ""
        return true
    }

    private fun isValidAccountHolderName(): Boolean {
        val pattern = Regex("^[a-zA-Z\\s.]+$")

        if(!pattern.matches(accountHolderName)){
            accountNameError = "Invalid Name format"
            return false
        }
        accountNameError = ""
        return true
    }

    private fun isValidAccountNumber(): Boolean {
        val pattern = Regex("^\\d{9,18}$")
        if(!pattern.matches(accountNumber)){
            accountNumberError = "Invalid Account Number format"
            return false
        }
        accountNumberError = ""
        return true
    }

    fun validateInputs(): Boolean{

        val isValidFssai = isValidFSSAI()
        val isValidPan = isValidPAN()
        val isValidGstin = isValidGSTIN()
        val isValidAccNum = isValidAccountNumber()
        val isValidAccName = isValidAccountHolderName()

        return (isValidAccName && isValidAccNum && isValidPan && isValidFssai && isValidGstin)
    }
    fun areAllDocumentsPresent() : Boolean{

        Log.d("documents","$panCardDocument $fssaiDocument $gstinDocument")

        if(panCardDocument == null || fssaiDocument == null || gstinDocument == null){
            areAllDocumentsPresentError = "Please Upload all Documents"
            return false
        }
        areAllDocumentsPresentError = ""
        return true
    }
}

