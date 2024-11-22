package com.example.preperly.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.preperly.R
import com.example.preperly.viewmodels.RestaurantDashboardViewModel

// Data classes
data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    var isAvailable: Boolean = true
)


@Composable
fun RestaurantDashboard(
    viewModel: RestaurantDashboardViewModel
) {
    viewModel.initialItems()

    Scaffold(
        topBar = { DashboardTopBar(
            isOnline = viewModel.isOnline,
            hasNotifications = viewModel.hasNotifications,
            onToggleOnline = viewModel::toggleOnlineStatus
        ) },
        bottomBar = { DashboardBottomNav(onNavigate = {}) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFFB71C1C)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Scan QR",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photos Section
            item {
                Text(
                    text = "Photos",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .height(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhotoButton(
                        icon = Icons.Default.Add,
                        text = "Add photos",
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    PhotoButton(
                        icon = Icons.Default.Edit,
                        text = "Edit photos",
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    PhotoButton(
                        icon = Icons.Default.Add,
                        text = "View photos",
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Menu Section
            item {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(viewModel.menuItems) { menuItem ->
                MenuItemCard(
                    item = menuItem,
                    onEditClick = {  },
                    onToggleAvailability = { viewModel.toggleAvailability(menuItem.id) }
                )
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
private fun PhotoButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = onClick,
        modifier.height(120.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB71C1C)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    onEditClick: () -> Unit,
    onToggleAvailability: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp)
            )

            // Item Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Edit and Availability
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(onClick = onEditClick,) {
                    Text(
                        "Edit",
                        color = Color(0xFFB71C1C),


                    )
                }
                Switch(
                    checked = item.isAvailable,
                    onCheckedChange = { onToggleAvailability() },

                )
                Text("Available")
            }
        }
    }
}

@Composable
private fun DashboardBottomNav(onNavigate: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, "Orders") },
            label = { Text("Orders") },
            selected = false,
            onClick = { onNavigate("orders") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Build, "Dashboard") },
            label = { Text("Dashboard") },
            selected = false,
            onClick = { onNavigate("dashboard") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, "Menu") },
            label = { Text("Menu") },
            selected = false,
            onClick = { onNavigate("menu") }
        )
    }
}


@Preview(
    name = "Restaurant Dashboard",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun RestaurantDashboardPreview() {
    val sampleViewModel = RestaurantDashboardViewModel().apply {
        isOnline = true
        hasNotifications = true
        menuItems = listOf(
            MenuItem(
                id = "1",
                name = "Margherita Pizza",
                description = "Classic tomato and mozzarella",
                imageUrl = "https://example.com/pizza.jpg",
                isAvailable = true
            ),
            MenuItem(
                id = "2",
                name = "Caesar Salad",
                description = "Romaine lettuce with Caesar dressing",
                imageUrl = "https://example.com/salad.jpg",
                isAvailable = false
            )
        )
    }

    MaterialTheme {
        RestaurantDashboard(
            viewModel = sampleViewModel
        )
    }
}