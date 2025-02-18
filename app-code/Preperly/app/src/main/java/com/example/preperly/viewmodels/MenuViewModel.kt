package com.example.preperly.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.preperly.RetrofitInstance
import com.example.preperly.datamodels.Category
import com.example.preperly.datamodels.MenuItem
import com.example.preperly.datamodels.UserResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel : ViewModel() {

    var categories by mutableStateOf<List<Category>>(emptyList())
    var menuItems by mutableStateOf<List<MenuItem>>(emptyList())
    var currentStep by mutableIntStateOf(5)
    var registrationResponse by mutableStateOf(UserResponse(false,"",0))

    fun addCategory(category: Category) {
        categories = categories + category
    }

    fun addSubCategory(category: Category, subCategory: String) {
        categories = categories.map {
            if (it == category) {
                it.copy(subCategories = (it.subCategories + subCategory).toMutableList())
            } else it
        }

        Log.d("categories",categories.toString())
    }

    fun addMenuItem(item: MenuItem) {
        menuItems = menuItems + item
    }

    fun updateMenuItem(oldItem: MenuItem, newItem: MenuItem) {
        menuItems = menuItems.map { if (it == oldItem) newItem else it }
    }

    fun toggleAvailability(itemId: Int) {
        menuItems = menuItems.map {
            if (it.id == itemId) it.copy(isAvailable = !it.isAvailable)
            else it
        }
    }
    fun resMenuToApi(phoneNumber: String){

        viewModelScope.launch {

            RetrofitInstance.userRegisterApi.addMenu(phoneNumber,menuItems).enqueue(object :
                Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        // Handle success
                        val userResponse = response.body()
                        // Update UI or notify success
                        if(userResponse?.status == 200){
                            Log.d("UserResponse",userResponse.message)
                            registrationResponse = userResponse
                        }
                    } else {
                        // Handle error
                        Log.d("UserResponse", "Error: ${response.message()}")
                        registrationResponse = UserResponse(success = response.isSuccessful, message = response.message(), status = response.code())
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    // Handle failure
                    Log.d("UserResponse", "No response from API: ${t.message}")
                    registrationResponse = t.message?.let { UserResponse(success = false, message = it, status = 500) }!!
                }
            })
        }
    }
}