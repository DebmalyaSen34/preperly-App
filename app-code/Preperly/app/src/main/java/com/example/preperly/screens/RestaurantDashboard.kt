package com.example.preperly.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.preperly.R
import com.example.preperly.viewmodels.AnalyticsViewModel
import com.example.preperly.viewmodels.MenuViewModel
import com.example.preperly.viewmodels.OrderDetailsScreenViewModel
import com.example.preperly.viewmodels.RestaurantDashboardViewModel
import com.example.preperly.viewmodels.RestaurantMenuScreenViewModel
import com.example.preperly.viewmodels.UploadImagesViewModel

@Composable
fun RestaurantDashboard(
    viewModel: RestaurantDashboardViewModel,
    addedMenuViewModel: MenuViewModel,
    viewModel2: OrderDetailsScreenViewModel,
    viewModel3: AnalyticsViewModel,
    viewModel4: UploadImagesViewModel
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { DashboardTopBar(
            isOnline = viewModel.isOnline,
            hasNotifications = viewModel.hasNotifications,
            onToggleOnline = viewModel::toggleOnlineStatus
        ) },
        bottomBar = { DashboardBottomNav(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFFB71C1C)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.scan_barcode),
                    contentDescription = "Scan QR",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        NavHost(navController = navController,
            startDestination = "menu",
            modifier = Modifier.padding(padding)
            ) {
            composable("menu") {
                RestaurantMenu(
                    addedMenuViewModel = addedMenuViewModel,
                    imagesViewModel = viewModel4
                )
            }
            composable("orders") {
                OrdersScreen(
                    viewModel = viewModel2,
                    navController = navController
                    )
            }
            composable("orderDetails/{orderId}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                orderId?.let { id ->
                    IndividualOrderDetails(
                        orderId = id ,
                        viewModel = viewModel2,
                        onBack = {navController.popBackStack()}
                    )
                }
            }
            composable("analytics") {
                AnalyticsDashboardScreen(viewModel = viewModel3)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    isOnline: Boolean,
    hasNotifications: Boolean,
    onToggleOnline: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Switch(
                    checked = isOnline,
                    onCheckedChange = { onToggleOnline() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50)
                    )
                )
                Text(if (isOnline) "Online" else "Offline")
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (hasNotifications) {
                        Badge(containerColor = Color.Red) // Customize the badge color if needed
                    }
                }
            ) {
                IconButton(onClick = { /* Handle notifications */ }) {
                    Icon(Icons.Default.Notifications, "Notifications")
                }
            }
            IconButton(onClick = { /* Handle profile */ }) {
                Icon(Icons.Default.Person, "Profile")
            }
        }
    )

}


@Composable
private fun DashboardBottomNav(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(painter = painterResource(id = R.drawable.bag),
                    "Orders")
                   },
            label = { Text("Orders") },
            selected = currentRoute == "orders",
            onClick = { navController.navigate("orders") }
        )
        NavigationBarItem(
            icon = { 
                Icon(painter = painterResource(id = R.drawable.analytics), 
                "Analytics")
                   },
            label = { Text("Analytics") },
            selected = currentRoute == "analytics",
            onClick = { navController.navigate("analytics") }
        )
        NavigationBarItem(
            icon = { 
                Icon(painter = painterResource(id = R.drawable.menu), 
                    "Menu")
                   },
            label = { Text("Menu") },
            selected = currentRoute == "menu",
            onClick = { navController.navigate("menu") }
        )
    }
}

