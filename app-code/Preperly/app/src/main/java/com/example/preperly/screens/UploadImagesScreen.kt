package com.example.preperly.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.preperly.R
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.UploadImagesViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale



@Composable
fun UploadImagesScreen(
    viewModel: UploadImagesViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val restaurantLogoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.restaurantLogo += uris
    }

    val restaurantImagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.restaurantImages += uris
    }

    var showViewImagesScreen by remember { mutableStateOf(false) }

    if (showViewImagesScreen) {
        ViewImagesScreen(
            onBack = { showViewImagesScreen = false },
            onReupload = { uri,whichImage->
                if(whichImage == "Logo"){
                    viewModel.onDeleteImage(uri,"Logo")
                    restaurantLogoPicker.launch("image/*")
                }else{
                    viewModel.onDeleteImage(uri,"RImages")
                    restaurantImagesPicker.launch("image/*")
                }
            },
            onDelete = {uri,whichImage -> viewModel.onDeleteImage(uri,whichImage) },
            viewModel = viewModel
        )
    }else{
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
                title = "Restaurant Logo*",
                imageCount = viewModel.restaurantLogo.size,
                onAddClick = { restaurantLogoPicker.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ImageUploadSection(
                title = "Restaurant images*",
                imageCount = viewModel.restaurantImages.size,
                onAddClick = { restaurantImagesPicker.launch("image/*") }
            )

            TextButton(
                onClick = {
                    showViewImagesScreen = true
                },
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
                        if(viewModel.restaurantImages.isNotEmpty() && viewModel.restaurantLogo.isNotEmpty()){
                            onNext()
                        }else{
                            Toast.makeText(context,"Please Upload Images...", Toast.LENGTH_SHORT).show()
                        }
//                        onNext()
//                        Toast.makeText(context,"Clicked", Toast.LENGTH_SHORT).show()
//                        Log.d("Next Button","Clicked")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = myRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next")
                }
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

@Composable
fun ViewImagesScreen(
    onBack: () -> Unit,
    onReupload: (Uri,String) -> Unit,
    onDelete: (Uri,String) -> Unit,
    viewModel: UploadImagesViewModel
) {
    var isEditMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Edit Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Images",
                style = MaterialTheme.typography.headlineSmall
            )
            TextButton(
                onClick = { isEditMode = !isEditMode },
                colors = ButtonDefaults.textButtonColors(contentColor = myRed)
            ) {
                Text(if (isEditMode) "Done" else "Edit")
            }
        }

        // Restaurant Logos Section
        Text(
            text = "Restaurant Logos",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (viewModel.restaurantLogo.isEmpty()) {
            EmptyImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } else {

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.restaurantLogo) { uri ->
                    ImageCard(
                        uri = uri,
                        isEditMode = isEditMode,
                        onDelete = { onDelete(uri,"Logo") },
                        onReupload = { onReupload(uri,"Logo") },
                        modifier = Modifier
                            .width(300.dp)
                            .height(200.dp)
                    )
                }
            }
        }

        // Restaurant Images Section
        Text(
            text = "Restaurant Images",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (viewModel.restaurantImages.isEmpty()) {
            EmptyImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.restaurantImages) { uri ->
                    ImageCard(
                        uri = uri,
                        isEditMode = isEditMode,
                        onDelete = { onDelete(uri,"RImages") },
                        onReupload = { onReupload(uri,"RImages") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            }
        }

        // Back Button
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = myRed),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Back")
        }
    }
}

@Composable
fun ImageCard(
    uri: Uri,
    isEditMode: Boolean,
    onDelete: (Uri) -> Unit,
    onReupload: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {

        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isEditMode) {

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                    ){
                    IconButton(
                        onClick = {onDelete(uri)}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = myRed

                        )
                    }
                    IconButton(
                        onClick = {onReupload(uri)},
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "ReUpload",
                            tint = myRed

                        )
                    }
                }

            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }
    }
}

@Composable
fun EmptyImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = R.drawable.addphotos),
            contentDescription = "No image",
            modifier = Modifier.size(48.dp),
            colorFilter = ColorFilter.tint(Color.Red)
        )
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