package com.example.preperly.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.preperly.viewmodels.RestaurantDashboardViewModel

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    var isAvailable: Boolean = true
)

@Composable
fun RestaurantMenu(viewModel: RestaurantDashboardViewModel){

    viewModel.initialItems()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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