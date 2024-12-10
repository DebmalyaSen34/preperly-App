package com.example.preperly.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.preperly.datamodels.Order
import com.example.preperly.datamodels.OrderStatus
import com.example.preperly.viewmodels.OrderDetailsScreenViewModel

@Composable
fun OrdersScreen(viewModel : OrderDetailsScreenViewModel){
    var selectedTab by remember { mutableStateOf(OrderStatus.ACTIVE)}
    viewModel.intializeOrderDetails()
    Column(modifier = Modifier.fillMaxSize()){

        TabRow(
            selectedTabIndex = OrderStatus.entries.indexOf(selectedTab)
        ) {
            OrderStatus.entries.forEach { status ->
                Tab(
                    selected = selectedTab == status,
                    onClick = { selectedTab = status },
                    text = { Text(status.name) }
                )
            }
        }

        val filteredOrders = viewModel.orderdetails.filter { it.status == selectedTab }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredOrders) { order ->
                OrderCard(order = order)
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        onClick = { /* Navigate to order details */ },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order ID", color = Color.Gray)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "View Details")
            }

            Text(
                text = order.orderId,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Number of people: ${order.numberOfPeople}")
            Text("Time: ${order.time}")
            Text("Items: ${order.totalItems} | Amount: ${order.amount}/-")
        }
    }
}