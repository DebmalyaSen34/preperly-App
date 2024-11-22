package com.example.preperly.datamodels

import android.net.Uri

data class Category(
    val name: String,
    val subCategories: MutableList<String> = mutableListOf()
)

data class MenuItem(
    val name: String,
    val description: String,
    val imageUri: Uri,
    val category: String,
    val subCategory: String,
    val itemType: String,
    val containsDairy: Boolean
)