package com.example.preperly.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.clickable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import com.example.preperly.R
import com.example.preperly.datamodels.RevenueData
import com.example.preperly.datamodels.TimeFilter
import com.example.preperly.datamodels.Transaction
import com.example.preperly.viewmodels.AnalyticsViewModel

@Composable
fun TimeFilterDropdown(
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = selectedFilter.name.lowercase()
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Select time filter",
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TimeFilter.entries.forEach { filter ->
                DropdownMenuItem(
                    text = {
                        Text(filter.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                        )
                    },
                    onClick = {
                        onFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AnalyticsDashboardScreen(viewModel: AnalyticsViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard(
                    title = "Orders",
                    value = viewModel.orders.toString(),
                    imageId = R.drawable.cart,
                    backgroundColor = Color(0xFFE53935),
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    title = "Revenue",
                    value = "${viewModel.revenue}k",
                    imageId = R.drawable.rupee2,
                    backgroundColor = Color(0xFF4285F4),
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    title = "Items",
                    value = viewModel.items.toString(),
                    imageId = R.drawable.order_box,
                    backgroundColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Orders Summary
        item {
            OrdersSummaryCard(viewModel)
        }

        // Revenue Graph
        item {
            RevenueCard(viewModel)
        }

        // Latest Transactions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Latest Transactions",
                        style = MaterialTheme.typography.titleLarge
                    )

                    viewModel.transactions.forEach { transaction ->
                        TransactionRow(transaction)
                    }

                    Text(
                        "More...",
                        color = Color(0xFFE53935),
                        modifier = Modifier
                            .clickable { /* Handle more click */ }
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    imageId: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    value,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    painter = painterResource(id = imageId),
                    contentDescription = title,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun OrderStatusRow(status: String, count: Int, dotColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = dotColor)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "$status: $count",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RevenueGraph(data: List<RevenueData>, modifier: Modifier = Modifier) {
    val textPaint = Paint().apply {
        color = android.graphics.Color.GRAY
        textAlign = Paint.Align.RIGHT
        textSize = 30f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxAmount = data.maxOf { it.amount }
        val padding = 50f
        val graphWidth = width - padding
        val graphHeight = height - padding

        // Draw y-axis
        drawLine(
            Color.LightGray,
            Offset(padding, 0f),
            Offset(padding, graphHeight),
            strokeWidth = 2f
        )

        // Draw x-axis
        drawLine(
            Color.LightGray,
            Offset(padding, graphHeight),
            Offset(width, graphHeight),
            strokeWidth = 2f
        )

        // Draw y-axis labels
        val yStep = maxAmount / 5
        for (i in 0..5) {
            val y = graphHeight - (i * graphHeight / 5f)
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    "${(i * yStep).toInt()}",
                    padding - 10f,
                    y + textPaint.textSize / 3,
                    textPaint
                )
            }
            drawLine(
                Color.LightGray,
                Offset(padding, y),
                Offset(width, y),
                strokeWidth = 1f
            )
        }

        // Draw data points and lines
        val points = data.mapIndexed { index, item ->
            Offset(
                x = padding + (graphWidth * index / (data.size - 1)),
                y = graphHeight - (graphHeight * item.amount / maxAmount)
            )
        }

        // Draw lines connecting points
        for (i in 0 until points.size - 1) {
            drawLine(
                Color(0xFF4CAF50),
                points[i],
                points[i + 1],
                strokeWidth = 3f
            )
        }

        // Draw points
        points.forEach { point ->
            drawCircle(
                Color(0xFF4CAF50),
                radius = 6.dp.toPx(),
                center = point
            )
        }

        // Draw x-axis labels
        data.forEachIndexed { index, item ->
            val x = padding + (graphWidth * index / (data.size - 1))
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    item.day,
                    x,
                    height - 10f,
                    textPaint.apply {
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                transaction.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                transaction.date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Text(
            "+${transaction.amount}/-",
            color = Color(0xFF4CAF50),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun OrdersSummaryCard(viewModel: AnalyticsViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Orders Summary",
                    style = MaterialTheme.typography.titleLarge
                )
                TimeFilterDropdown(
                    selectedFilter = viewModel.ordersSummaryTimeFilter,
                    onFilterSelected = viewModel::updateOrdersSummaryTimeFilter
                )
            }

            OrderStatusRow("Active", viewModel.activeOrders, Color(0xFF4CAF50))
            OrderStatusRow("Completed", viewModel.completedOrders, Color(0xFFE53935))
            OrderStatusRow("Cancelled", viewModel.cancelledOrders, Color.Gray)
        }
    }
}

@Composable
fun RevenueCard(viewModel: AnalyticsViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Revenue",
                    style = MaterialTheme.typography.titleLarge
                )
                TimeFilterDropdown(
                    selectedFilter = viewModel.revenueTimeFilter,
                    onFilterSelected = viewModel::updateRevenueTimeFilter
                )
            }

            RevenueGraph(
                data = viewModel.revenueData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}