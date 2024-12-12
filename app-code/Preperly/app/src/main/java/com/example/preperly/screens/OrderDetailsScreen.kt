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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.preperly.datamodels.Order
import com.example.preperly.datamodels.OrderItem
import com.example.preperly.datamodels.OrderStatus
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.OrderDetailsScreenViewModel

@Composable
fun OrdersScreen(viewModel : OrderDetailsScreenViewModel,
                 navController: NavController){

    var selectedTab by remember { mutableStateOf(OrderStatus.ACTIVE)}

    viewModel.initializeOrderDetails()

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
                OrderCard(order = order,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    navController: NavController) {
    Card(
        onClick = {
            navController.navigate("orderDetails/${order.orderId}")
        },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualOrderDetails(
    orderId: String,
    viewModel: OrderDetailsScreenViewModel,
    onBack: () -> Unit
){
    val order = viewModel.getOrderDetailsById(orderId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Order Details", color = myRed)

                        TextButton(
                            onClick = onBack,
                            colors = ButtonDefaults.textButtonColors(contentColor = myRed),
                        ) {
                            Text("Back")
                        }
                    }

                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Order Information
            item {
                if (order != null) {
                    OrderInfoSection(orderDetails = order)
                }
            }

            // Divider
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.LightGray
                )
            }

            // Items Header
            item {
                Text(
                    text = "Items:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Items List
            if (order != null){
                items(order.itemsList) { item ->
                    OrderItemRow(item)
                }
            }
        }
    }

}

@Composable
fun OrderInfoSection(orderDetails: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OrderInfoRow("Order ID:", orderDetails.orderId)
        OrderInfoRow("Number of people:", "${orderDetails.numberOfPeople}")
        OrderInfoRow("Time:", orderDetails.time)
        OrderInfoRow(
            "Items:",
            "${orderDetails.totalItems} | Amount: ${String.format("%.2f", orderDetails.amount)}/-"
        )
    }
}

@Composable
fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = value)
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.quantity} x ${item.name}",
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${String.format("%.2f", item.price)}/-",
            fontWeight = FontWeight.Medium
        )
    }
}