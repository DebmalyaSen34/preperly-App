package com.example.preperly.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.preperly.RetrofitInstance
import com.example.preperly.datamodels.DocumentData
import com.example.preperly.datamodels.UserResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DocumentsUploadViewModel: ViewModel() {

    var fssaiDocument = mutableStateOf(File(""))

    var gstinDocument = mutableStateOf(File(""))

    var panCardDocument = mutableStateOf(File(""))

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

    var registrationResponse by mutableStateOf(UserResponse("",0))

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

        if(panCardDocument.value.path.isEmpty() || fssaiDocument.value.path.isEmpty() || gstinDocument.value.path.isEmpty()){
            areAllDocumentsPresentError = "Please Upload all Documents"
            return false
        }
        areAllDocumentsPresentError = ""
        return true
    }

    private fun convertToRequestBody(document: File, fileName: String): MultipartBody.Part {
        val filePart = MultipartBody.Part.createFormData(
            fileName,  // Key name expected by the server
            document.name,        // Original file name
            document.asRequestBody("application/pdf".toMediaType()) // File as RequestBody
        )
        return filePart
    }

//    private fun createDocumentData(): DocumentData {
//
//        return DocumentData(
//
//            fssaiLicence = fssaiLicence.toRequestBody("text/plain".toMediaType()),
//            gstin = gstin.toRequestBody("text/plain".toMediaType()),
//            panCard = panCard.toRequestBody("text/plain".toMediaType()),
//            accountHolderName = accountHolderName.toRequestBody("text/plain".toMediaType()),
//            accountNumber = accountNumber.toRequestBody("text/plain".toMediaType()),
//            fssaiDocument = convertToRequestBody(fssaiDocument.value,"fssaiDoc"),
//            gstinDocument = convertToRequestBody(gstinDocument.value, "gstinDoc"),
//            panCardDocument = convertToRequestBody(panCardDocument.value, "panDoc")
//        )
//    }

    fun restaurantDocsToApi(phoneNumber: String) {

        viewModelScope.launch {
            RetrofitInstance.userRegisterApi.restaurantDocData(
                phoneNumber = phoneNumber,
                fssaiLicence = fssaiLicence.toRequestBody("text/plain".toMediaType()),
                gstin = gstin.toRequestBody("text/plain".toMediaType()),
                panCard = panCard.toRequestBody("text/plain".toMediaType()),
                accountHolderName = accountHolderName.toRequestBody("text/plain".toMediaType()),
                accountNumber = accountNumber.toRequestBody("text/plain".toMediaType()),
                fssaiDocument = convertToRequestBody(fssaiDocument.value,"fssaiDoc"),
                gstinDocument = convertToRequestBody(gstinDocument.value, "gstinDoc"),
                panCardDocument = convertToRequestBody(panCardDocument.value, "panDoc")
            ).enqueue(object :
                Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        // Handle success
                        val userResponse = response.body()
                        // Update UI or notify success
                        if(userResponse?.status == 200){
                            Log.d("UserResponse",userResponse.message)
                            registrationResponse = UserResponse(message = userResponse.message, status = userResponse.status)
                        }
                    } else {
                        // Handle error
                        Log.d("UserResponse", "Error: ${response.message()}")
                        registrationResponse = UserResponse(message = response.message(), status = response.code())
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    // Handle failure
                    Log.d("UserResponse", "No response from API: ${t.message}")
                    registrationResponse = t.message?.let { UserResponse(message = it, status = 500) }!!
                }
            })
        }
    }
}

