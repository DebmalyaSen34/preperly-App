package com.example.preperly.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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

            IconButton(onClick = { /* Handle click */ }){
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }


            Text(
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

            IconButton(onClick = { /* Handle click */ }){
                Image(
                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }

            Text(
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

            IconButton(onClick = { /* Handle click */ }){
                Image(

                    painter = painterResource(id = R.drawable.addphotos),
                    contentDescription = "Add images",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(myRed),
                )
            }
            Text(
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = myRed),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
            }
        }
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