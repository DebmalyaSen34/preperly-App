package com.example.preperly.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.preperly.datamodels.Category
import com.example.preperly.datamodels.MenuItem

class MenuViewModel : ViewModel() {

    var categories by mutableStateOf<List<Category>>(emptyList())
    var menuItems by mutableStateOf<List<MenuItem>>(emptyList())
    var currentStep by mutableIntStateOf(5)

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
}