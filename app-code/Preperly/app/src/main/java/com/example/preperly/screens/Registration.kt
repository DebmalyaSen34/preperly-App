package com.example.preperly.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.AnalyticsViewModel
import com.example.preperly.viewmodels.DocumentsUploadViewModel
import com.example.preperly.viewmodels.MenuViewModel
import com.example.preperly.viewmodels.OrderDetailsScreenViewModel
import com.example.preperly.viewmodels.RestaurantDashboardViewModel
import com.example.preperly.viewmodels.RestaurantDetailsViewModel
import com.example.preperly.viewmodels.RestaurantMenuScreenViewModel
import com.example.preperly.viewmodels.RestaurantTypeViewModel
import com.example.preperly.viewmodels.SharedViewModel
import com.example.preperly.viewmodels.UploadImagesViewModel

@Composable
fun RegisterNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel1: RestaurantDetailsViewModel,
    viewModel2: RestaurantTypeViewModel,
    viewModel3: DocumentsUploadViewModel,
    viewModel4: UploadImagesViewModel,
    viewModel5: MenuViewModel,
    viewModel6: RestaurantDashboardViewModel,
    viewModel7: RestaurantMenuScreenViewModel,
    viewModel8: OrderDetailsScreenViewModel,
    viewModel9: AnalyticsViewModel,
    sharedViewModel: SharedViewModel
    ){
    NavHost(navController = navController, startDestination = "step1"){
        composable("step1") {
            RestaurantRegistrationForm(
                viewModel = viewModel1,
                sharedViewModel = sharedViewModel,
                onNext = {navController.navigate("step2")}
            )
        }
        composable("step2") {
            RestaurantTypeAndTimingsScreen(
                viewModel = viewModel2,
                sharedViewModel = sharedViewModel,
                onNext = { navController.navigate("step3") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step3") {
            DocumentsUploadScreen(
                viewModel = viewModel3,
                sharedViewModel = sharedViewModel,
                onNext = { navController.navigate("step4") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step4") {
            UploadImagesScreen(
                viewModel = viewModel4,
                sharedViewModel = sharedViewModel,
                onNext = { navController.navigate("step5") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step5") {
            MenuUploadScreen(
                viewModel = viewModel5,
                sharedViewModel = sharedViewModel,
                onNext = { navController.navigate("step6") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step6") {
            RestaurantDashboard(
                viewModel = viewModel6,
                viewModel1 = viewModel7,
                viewModel2 = viewModel8,
                viewModel3 = viewModel9
            )
        }
    }
}

@Composable
fun ProgressIndicator(currentStep: Int) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var j = 1
        for (i in 1..5) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = when {
                        j < currentStep ->  myRed
                        j == currentStep -> myRed.copy(alpha = 0.5f)
                        else -> Color.LightGray
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (j < currentStep) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White
                            )
                        } else {
                            Text(
                                text = String.format("%02d", j),
                                color = if (j <= currentStep) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
            if(j !=5){
                HorizontalDivider(modifier = Modifier
                    .padding(vertical = 20.dp)
                    .size(if(screenWidth > 400.dp) 70.dp else 30.dp)
                )
            }
            j++
        }
    }
}

@Preview
@Composable
fun PreviewProgressBar(){
    ProgressIndicator(1)
}
