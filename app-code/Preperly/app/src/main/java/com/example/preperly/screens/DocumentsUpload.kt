package com.example.preperly.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.preperly.viewmodels.DocumentsUploadViewModel
import com.example.preperly.R
import com.example.preperly.ui.theme.myRed

@Composable
fun DocumentsUploadScreen(
    viewModel: DocumentsUploadViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current


    val fssaiLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        viewModel.fssaiDocument = uri

        uri.let {
            Toast.makeText(context, "FSSAI Document Selected: $uri", Toast.LENGTH_SHORT).show()
        }
    }
    val gstinLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        viewModel.gstinDocument = uri

        uri.let {
            Toast.makeText(context, "GSTIN Document Selected: $uri", Toast.LENGTH_SHORT).show()
        }
    }
    val pandCardLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        viewModel.panCardDocument = uri

        uri.let {
            Toast.makeText(context, "Pan Card Document Selected: $uri", Toast.LENGTH_SHORT).show()
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
            placeholder = { Text("Enter Licence ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){

            IconButton(onClick = { /* Handle click */
                fssaiLauncher.launch(arrayOf("application/pdf"))
            }){
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }

            viewModel.fssaiDocument?.let {
                UploadSuccess()
            } ?: Text(
                "Add FSSAI document*",
                color = myRed,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.gstin,
            onValueChange = { viewModel.updateGstin(it) },
            label = { Text("GSTIN*") },
            placeholder = { Text("Enter GSTIN ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){

            IconButton(onClick = { /* Handle click */
                gstinLauncher.launch(arrayOf("application/pdf"))
            }){
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }

            viewModel.gstinDocument?.let {
                UploadSuccess()
            } ?: Text(
                "Add Latest GSTIN Filed document*",
                color = myRed,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.panCard,
            onValueChange = { viewModel.updatePanCard(it) },
            label = { Text("Pan card*") },
            placeholder = { Text("Pan card number") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){

            IconButton(onClick = { /* Handle click */
                pandCardLauncher.launch(arrayOf("application/pdf"))
            }){
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }
            viewModel.panCardDocument?.let {
                UploadSuccess()
            } ?: Text(
                "Add Pan card photo*",
                color = myRed,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Bank account details*",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.accountHolderName,
            onValueChange = { viewModel.updateAccountHolderName(it)},
            label = { Text("Account holder's name*") },
            placeholder = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.accountNumber,
            onValueChange = { viewModel.updateAccountNumber(it) },
            label = { Text("Account Number*") },
            placeholder = { Text("Number") },
            modifier = Modifier.fillMaxWidth()
        )

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
fun UploadSuccess(){
    Row{
        Text(
            "Uploaded Successfully",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic
        )
        Image(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "completed",
            colorFilter = ColorFilter.tint(Color.Green))
    }
}
@Preview(showBackground = true)
@Composable
fun DocumentsUploadPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        DocumentsUploadScreen(
            viewModel = DocumentsUploadViewModel(),
            onNext = {navController.navigate("step4")},
            onBack = {navController.popBackStack()}
        )
    }
}