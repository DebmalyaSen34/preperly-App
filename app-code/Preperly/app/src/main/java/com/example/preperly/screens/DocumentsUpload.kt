package com.example.preperly.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.preperly.viewmodels.DocumentsUploadViewModel
import com.example.preperly.R
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.SharedViewModel
import java.io.File

@Composable
fun DocumentsUploadScreen(
    viewModel: DocumentsUploadViewModel,
    sharedViewModel: SharedViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    Toast.makeText(context,sharedViewModel.getPhoneNumber(),Toast.LENGTH_SHORT).show()

    var fssaiUri by remember { mutableStateOf<Uri?>(null) }
    var gstinUri by remember { mutableStateOf<Uri?>(null) }
    var panCardUri by remember { mutableStateOf<Uri?>(null) }

    fun getFileFromUri(context: Context, uri: Uri?,fileName: String): File {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, fileName)

        if(uri != null){
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }

    val fssaiLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            fssaiUri = uri
            viewModel.fssaiDocument.value = getFileFromUri(context, uri,"fssaiDoc")

            uri.let {
                Toast.makeText(context, "FSSAI Document Selected: $uri", Toast.LENGTH_SHORT).show()
                Log.d("fssai doc", viewModel.fssaiDocument.value.path.toString())
            }
        }
    val gstinLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            gstinUri = uri
            viewModel.gstinDocument.value = getFileFromUri(context, uri,"gstinDoc")

            uri.let {
                Toast.makeText(context, "GSTIN Document Selected: $uri", Toast.LENGTH_SHORT).show()
                Log.d("fssai doc", viewModel.gstinDocument.value.path.toString())
            }
        }
    val panCardLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            panCardUri = uri
            viewModel.panCardDocument.value =getFileFromUri(context, uri,"panDoc")

            uri.let {
                Toast.makeText(context, "Pan Card Document Selected: $uri", Toast.LENGTH_SHORT).show()
                Log.d("fssai doc", viewModel.panCardDocument.value.path.toString())
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        ProgressIndicator(currentStep = viewModel.currentStep)

        Text(
            "Documents Upload",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = viewModel.fssaiLicence,
            onValueChange = { viewModel.updateFssaiLicence(it) },
            label = { Text("FSSAI Licence*") },
            isError = viewModel.fssaiLicenceError.isNotBlank(),
            placeholder = { Text("Enter Licence ID") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        if (viewModel.fssaiLicenceError.isNotBlank()) {
            Text(text = viewModel.fssaiLicenceError, color = Color.Red, fontSize = 12.sp)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { /* Handle click */
                fssaiLauncher.launch(arrayOf("application/pdf"))
            }) {
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }

            if(viewModel.fssaiDocument.value.path.isNotBlank()){
                DocumentUploadSuccess(context, fssaiUri)
            }else{
                Text(
                    "Add FSSAI document*",
                    color = myRed,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.gstin,
            onValueChange = { viewModel.updateGstin(it) },
            label = { Text("GSTIN*") },
            placeholder = { Text("Enter GSTIN ID") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.gstinError.isNotBlank()
        )
        if (viewModel.gstinError.isNotBlank()) {
            Text(text = viewModel.gstinError, color = Color.Red, fontSize = 12.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { /* Handle click */
                gstinLauncher.launch(arrayOf("application/pdf"))
            }) {
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }

            if(viewModel.gstinDocument.value.path.isNotBlank()){
                DocumentUploadSuccess(context, gstinUri)

            }else{
                Text(
                "Add Latest GSTIN Filed document*",
                color = myRed,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.panCard,
            onValueChange = { viewModel.updatePanCard(it) },
            label = { Text("Pan card*") },
            placeholder = { Text("Pan card number") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.panCardError.isNotBlank()
        )
        if (viewModel.panCardError.isNotBlank()) {
            Text(text = viewModel.panCardError, color = Color.Red, fontSize = 12.sp)
        }


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { /* Handle click */
                panCardLauncher.launch(arrayOf("application/pdf"))
            }) {
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }

            if(viewModel.panCardDocument.value.path.isNotBlank()) {
                DocumentUploadSuccess(context, panCardUri)
            }else{
                Text(
                    "Add Pan card photo*",
                    color = myRed,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Bank account details*",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.accountHolderName,
            onValueChange = { viewModel.updateAccountHolderName(it) },
            label = { Text("Account holder's name*") },
            placeholder = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.accountNameError.isNotBlank()
        )
        if (viewModel.accountNameError.isNotBlank()) {
            Text(text = viewModel.accountNameError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.accountNumber,
            onValueChange = { viewModel.updateAccountNumber(it) },
            label = { Text("Account Number*") },
            placeholder = { Text("Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = viewModel.accountNumberError.isNotBlank()
        )

        if (viewModel.accountNumberError.isNotBlank()) {
            Text(text = viewModel.accountNumberError, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (viewModel.areAllDocumentsPresentError.isNotBlank()) {
            Text(
                text = viewModel.areAllDocumentsPresentError,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
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
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
//                    if(viewModel.validateInputs() && viewModel.areAllDocumentsPresent()){
//                        viewModel.restaurantDocsToApi(sharedViewModel.getPhoneNumber())
//                        onNext()
//                    }
                    onNext()
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
fun DocumentUploadSuccess(context: Context, pdfUri: Uri?) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween){

        Text(
            "Uploaded Successfully",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic
        )
        Image(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "completed",
            colorFilter = ColorFilter.tint(Color.Green)
        )

        TextButton(
            onClick = {
                if (pdfUri != null) {
                    openPdf(context,pdfUri)
                }
            }
        ){
            Text(
                text = "View",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = myRed,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

fun openPdf(context: Context,pdfUri: Uri) {
    try{
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri,"application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent,"Open PDF with"))
    }catch (e: Exception){
        Toast.makeText(context,"No application available to open PDF",Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun DocumentsUploadPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        DocumentsUploadScreen(
            viewModel = DocumentsUploadViewModel(),
            sharedViewModel = SharedViewModel(),
            onNext = {navController.navigate("step4")},
            onBack = {navController.popBackStack()}
        )
    }
}