package com.example.preperly.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UploadImagesViewModel : ViewModel(){

    var currentStep by mutableIntStateOf(4)

    var restaurantLogo = mutableStateListOf<Uri>()
    var restaurantImages = mutableStateListOf<Uri>()
    private var restaurantLogoFiles = mutableStateListOf<File>()
    private var restaurantImagesFiles = mutableStateListOf<File>()

    fun onDeleteImage(uri: Uri,whichImage: String){

        if(whichImage == "Logo"){
            restaurantLogo.remove(uri)
        }else{
            restaurantImages.remove(uri)
        }
    }

    fun saveImageFiles(context: Context){

        // Clear the existing file lists to avoid duplicates
        restaurantLogoFiles.clear()
        restaurantImagesFiles.clear()

        // Convert each Uri in restaurantLogo to a File and add it to restaurantLogoFiles
        for (uri in restaurantLogo) {
            val file = saveUriAsJpg(context, uri, "logo_${System.currentTimeMillis()}")
            file?.let { restaurantLogoFiles.add(it) }
        }

        // Convert each Uri in restaurantImages to a File and add it to restaurantImagesFiles
        for (uri in restaurantImages) {
            val file = saveUriAsJpg(context, uri, "image_${System.currentTimeMillis()}")
            file?.let { restaurantImagesFiles.add(it) }
        }

        println("Logo files: ${restaurantLogoFiles.map { it.absolutePath }}")
        println("Image files: ${restaurantImagesFiles.map { it.absolutePath }}")
    }

    private fun saveUriAsJpg(context: Context, uri: Uri, fileName: String): File? {
        return try {
            // Get InputStream from Uri
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                // Decode InputStream to Bitmap
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                // Create a destination file
                val file = File(context.cacheDir, "$fileName.jpg")
                // Save Bitmap to the destination file
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Save as JPEG with 100% quality
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                file // Return the file
            } else {
                null // InputStream couldn't be opened
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if an error occurs
        }
    }
}