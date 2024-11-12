package com.example.preperly.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.preperly.RetrofitInstance
import com.example.preperly.datamodels.User
import com.example.preperly.datamodels.UserResponse
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RestaurantDetailsViewModel : ViewModel() {

    var currentStep by mutableIntStateOf(1)
    // Restaurant details
    var restaurantName = mutableStateOf("")
        private set
    var restaurantNameError = mutableStateOf<String?>(null)
        private set

    var restaurantAddress = mutableStateOf("")
        private set
    var restaurantAddressError = mutableStateOf<String?>(null)
        private set

    // Contact details
    var phoneNumber = mutableStateOf("")
        private set
    var phoneNumberError = mutableStateOf<String?>(null)
        private set

    var alternateNumber = mutableStateOf("")
        private set
    var alternateNumberError = mutableStateOf<String?>(null)

    var email = mutableStateOf("")
        private set
    var emailError = mutableStateOf<String?>(null)
        private set

    // Password
    var password = mutableStateOf("")
        private set
    var confirmPassword = mutableStateOf("")
    private set

    var passwordError = mutableStateOf<String?>(null)
        private set
    var confirmPasswordError = mutableStateOf<String?>(null)
        private set

    // Owner details
    var ownerName = mutableStateOf("")
        private set
    var ownerNameError = mutableStateOf<String?>(null)
    private set

    var ownerPhoneNumber = mutableStateOf("")
        private set
    var ownerPhoneError = mutableStateOf<String?>(null)
    private set
    var ownerEmail = mutableStateOf("")
        private set

    var ownerEmailError = mutableStateOf<String?>(null)
        private set

    var receiveUpdatesOnWhatsApp = mutableStateOf(false)

    // Functions to update each field
    fun updateRestaurantName(newValue: String) {
        restaurantName.value = newValue
    }

    fun updateRestaurantAddress(newValue: String) {
        restaurantAddress.value = newValue
    }

    fun updatePhoneNumber(newValue: String) {
        phoneNumber.value = newValue
    }

    fun updateAlternateNumber(newValue: String) {
        alternateNumber.value = newValue
    }

    fun updateEmail(newValue: String) {
        email.value = newValue
    }

    fun updatePassword(newValue: String) {
        password.value = newValue
    }

    fun updateConfirmPassword(newValue: String) {
        confirmPassword.value = newValue
    }

    fun updateOwnerName(newValue: String) {
        ownerName.value = newValue
    }

    fun updateOwnerPhoneNumber(newValue: String) {
        ownerPhoneNumber.value = newValue
    }

    fun updateOwnerEmail(newValue: String) {
        ownerEmail.value = newValue
    }

    fun updateReceiveUpdatesOnWhatsApp(newValue: Boolean) {
        receiveUpdatesOnWhatsApp.value = newValue
    }

    // Validation logic for each field
    private fun validateRestaurantName(): Boolean {
        if (restaurantName.value.isBlank()) {
            restaurantNameError.value = "Restaurant name cannot be empty"
            return false
        } else if (!isValidName(restaurantName.value)) {
            restaurantNameError.value = "Name must be 2-50 characters, and can only contain letters, spaces, ', or -."
            return false
        }
        restaurantNameError.value = ""
        return true
    }

    private fun validateRestaurantAddress(): Boolean {
        if (restaurantAddress.value.isBlank()) {
            restaurantAddressError.value = "Restaurant address cannot be empty"
            return false
        } else if(!isValidAddress(restaurantAddress.value)) {
            restaurantAddressError.value = "Address must be 5-100 characters, and can only contain letters, numbers, spaces, ,, ., or -."
            return false
        }
        restaurantAddressError.value = ""
        return true
    }

    private fun validatePhoneNumber(): Boolean {
        return if (phoneNumber.value.isBlank() || (phoneNumber.value.length < 10) && !phoneNumber.value.all { it.isDigit() }) {
            phoneNumberError.value = "Phone number must be at least 10 digits"
            false
        } else {
            phoneNumberError.value = null
            true
        }
    }

    fun validateAlternatePhone(): Boolean {
        return if (alternateNumber.value.isNotBlank() && alternateNumber.value.length < 10  && !phoneNumber.value.all { it.isDigit() }) {
            alternateNumberError.value = "Phone number must be at least 10 digits"
            false
        } else {
            alternateNumberError.value = null
            true
        }
    }


    private fun validateEmail(): Boolean {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            emailError.value = "Invalid email address"
            false
        } else {
            emailError.value = null
            true
        }
    }

    private fun validatePassword(): Boolean {

        passwordError.value = getPasswordError(password.value)

        return passwordError.value == null
    }

    private fun getPasswordError(password: String): String? {
        return when {
            password.length < 8 -> "Password should be at least 8 characters long."
            password.length > 20 -> "Password should not exceed 20 characters."
            !password.contains(Regex("[A-Z]")) -> "Password should contain at least one uppercase letter."
            !password.contains(Regex("[a-z]")) -> "Password should contain at least one lowercase letter."
            !password.contains(Regex("[0-9]")) -> "Password should contain at least one number."
            !password.contains(Regex("[!@#\$%^&*]")) -> "Password should contain at least one special character."
            else -> null // No errors
        }
    }

    private fun validateConfirmPassword(): Boolean {
        return if (confirmPassword.value != password.value) {
            confirmPasswordError.value = "Passwords do not match"
            false
        } else {
            confirmPasswordError.value = null
            true
        }
    }

    private fun validateOwnerName(): Boolean {
        if (ownerName.value.isBlank()) {
            ownerNameError.value = "Owner name cannot be empty"
            return false
        } else if(!isValidName(ownerName.value)) {
            ownerNameError.value = "Owner name must be 2-50 characters, and can only contain letters, spaces, ', or -"
            return false
        }

        ownerNameError.value = null
        return true
    }
    private fun validateOwnerPhone(): Boolean {
        return if (ownerPhoneNumber.value.isBlank() || ownerPhoneNumber.value.length < 10) {
            ownerPhoneError.value = "Phone number must be at least 10 digits"
            false
        } else {
            ownerPhoneError.value = null
            true
        }
    }
    fun validateOwnerEmail(): Boolean {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(ownerEmail.value).matches() && ownerEmail.value.isNotBlank()) {
            ownerEmailError.value = "Invalid email address"
            false
        } else {
            ownerEmailError.value = null
            true
        }
    }

    private fun isValidName(name: String): Boolean {

        val namePattern = Pattern.compile("^[a-zA-Z\\s'-]{2,50}$")
        return namePattern.matcher(name).matches()
    }

    private fun isValidAddress(address: String): Boolean {

        val addressPattern = Pattern.compile("^[a-zA-Z0-9\\s,.-]{5,100}$")
        return addressPattern.matcher(address).matches()
    }

    private fun createUser(): User {
        val user = User(
            restaurantName = restaurantName.value,
            restaurantAddress = restaurantAddress.value,
            phoneNumber = phoneNumber.value,
            alternateNumber = alternateNumber.value,
            email = email.value,
            password = password.value,
            ownerName = ownerName.value,
            ownerPhoneNumber = ownerPhoneNumber.value,
            ownerEmail = ownerEmail.value,
            receiveUpdatesOnWhatsApp = receiveUpdatesOnWhatsApp.value
        )
//        Log.d("UserData", user.toString()) // Log the user object to see the data being sent
        return user
    }

    fun registerUser(){

        viewModelScope.launch {
            val user = createUser()
            Log.d("UserData", user.toString())
            RetrofitInstance.userRegisterApi.createUser(user).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        // Handle success
                        val userResponse = response.body()
                        // Update UI or notify success

                        if(userResponse?.status == 200){
                            Log.d("UserResponse",userResponse.message)
                        }
                    } else {
                        // Handle error
                        Log.d("UserResponse", "Error: ${response.errorBody()?.string()}")

                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    // Handle failure
                    Log.d("UserResponse", "No response from API: ${t.message}")
                }
            })
        }
    }

    fun validateForm(): Boolean {
        val isResNameValid = validateRestaurantName()
        val isAddressValid = validateRestaurantAddress()
        val isResPhoneValid = validatePhoneNumber()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()
        val isOwnerNameValid = validateOwnerName()
        val isOwnerPhoneValid = validateOwnerPhone()

        return isResNameValid && isAddressValid && isResPhoneValid && isEmailValid && isPasswordValid && isConfirmPasswordValid && isOwnerPhoneValid && isOwnerNameValid
    }
}