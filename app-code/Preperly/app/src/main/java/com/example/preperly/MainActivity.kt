package com.example.preperly

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.preperly.screens.DocumentChecklistScreen
import com.example.preperly.screens.LoginScreen
import com.example.preperly.screens.PreperlyWelcomeScreen
import com.example.preperly.screens.RegisterNavHost
import com.example.preperly.screens.RestaurantRegistrationForm
import com.example.preperly.ui.theme.PreperlyTheme
import com.example.preperly.viewmodels.AnalyticsViewModel
import com.example.preperly.viewmodels.DocumentsUploadViewModel
import com.example.preperly.viewmodels.MenuViewModel
import com.example.preperly.viewmodels.OrderDetailsScreenViewModel
import com.example.preperly.viewmodels.RestaurantDashboardViewModel
import com.example.preperly.viewmodels.RestaurantDetailsViewModel
import com.example.preperly.viewmodels.RestaurantMenuScreenViewModel
import com.example.preperly.viewmodels.RestaurantTypeViewModel
import com.example.preperly.viewmodels.UploadImagesViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PreperlyTheme {
                PreperlyNavigation()
            }
        }
    }
}

@Composable
fun PreperlyNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Welcome"){
        composable("Welcome") { PreperlyWelcomeScreen(navController) }
        composable("Login") { LoginScreen() }
        composable("Started") { DocumentChecklistScreen(navController)  }
        composable("Register") { RegisterNavHost(
            viewModel1 = RestaurantDetailsViewModel(),
            viewModel2 = RestaurantTypeViewModel(),
            viewModel3 = DocumentsUploadViewModel(),
            viewModel4 = UploadImagesViewModel(),
            viewModel5 = MenuViewModel(),
            viewModel6 = RestaurantDashboardViewModel(),
            viewModel7 = RestaurantMenuScreenViewModel(),
            viewModel8 = OrderDetailsScreenViewModel(),
            viewModel9 = AnalyticsViewModel()
        )
        }
    }
}
