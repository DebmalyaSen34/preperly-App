package com.example.preperly.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.RestaurantTypeViewModel
import com.example.preperly.viewmodels.UploadImagesViewModel


@Composable
fun UploadImagesScreen(
    viewModel: UploadImagesViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val restaurantImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.restaurantImages += uris
    }

    val foodImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.foodImages += uris
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        ProgressIndicator(currentStep = viewModel.currentStep)

        Text(
            "Upload Images",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        ImageUploadSection(
            title = "Restaurant images*",
            imageCount = viewModel.restaurantImages.size,
            onAddClick = { restaurantImagePicker.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ImageUploadSection(
            title = "Food images*",
            imageCount = viewModel.foodImages.size,
            onAddClick = { foodImagePicker.launch("image/*") }
        )

        TextButton(
            onClick = { /* Handle view images */ },
            colors = ButtonDefaults.textButtonColors(contentColor = myRed),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("View images →")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    onBack()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    onNext()
                    Toast.makeText(context,"Clicked", Toast.LENGTH_SHORT).show()
                    Log.d("Next Button","Clicked")
                },
                colors = ButtonDefaults.buttonColors(containerColor = myRed),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ImageUploadSection(
    title: String,
    imageCount: Int,
    onAddClick: () -> Unit
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center // Centers content within the Box
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // Centers content within the Column
            ) {
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (imageCount == 0) "Add photos" else "Add more photos",
                        tint = myRed,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = if (imageCount == 0) "Add photos" else "$imageCount image(s) selected",
                    color = myRed,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun UploadImageScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        UploadImagesScreen(
            viewModel = UploadImagesViewModel(),
            onNext = {navController.navigate("step5")},
            onBack = {navController.popBackStack()}
        )
    }
}