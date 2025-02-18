package com.example.preperly.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.preperly.RetrofitInstance
import com.example.preperly.datamodels.UserResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UploadImagesViewModel : ViewModel(){

    var currentStep by mutableIntStateOf(4)

    var restaurantLogo = mutableStateListOf<Uri>()
    var restaurantImages = mutableStateListOf<Uri>()
    private var restaurantLogoFiles = mutableStateListOf<File>()
    private var restaurantImagesFiles = mutableStateListOf<File>()
    var registrationResponse by mutableStateOf(UserResponse(false,"",0))

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

    private fun convertFilesToMultipartBodyPart(files: List<File>, keyName: String): List<MultipartBody.Part> {
        return files.map { file ->
            MultipartBody.Part.createFormData(
                keyName, // Key name expected by the server
                file.name, // File name
                file.asRequestBody("image/jpg".toMediaTypeOrNull()) // Replace with the correct MIME type
            )
        }
    }

    fun restaurantImagesToApi(phoneNumber: String) {

        viewModelScope.launch {
            RetrofitInstance.userRegisterApi.uploadImages(
                phoneNumber = phoneNumber.toRequestBody("text/plain".toMediaType()),
                convertFilesToMultipartBodyPart(restaurantLogoFiles,"restaurantLogos"),
                convertFilesToMultipartBodyPart(restaurantImagesFiles,"restaurantImages")
            ).enqueue(object :
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