package com.example.preperly.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UploadImagesViewModel : ViewModel(){

    var currentStep by mutableIntStateOf(4)

    var restaurantImages by mutableStateOf<List<Uri>>(emptyList())
    var foodImages by mutableStateOf<List<Uri>>(emptyList())

}