package com.example.preperly.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.preperly.datamodels.RevenueData
import com.example.preperly.datamodels.TimeFilter
import com.example.preperly.datamodels.Transaction


// ViewModel
class AnalyticsViewModel : ViewModel() {
    var orders by mutableIntStateOf(10)
    var revenue by mutableFloatStateOf(20.5f)
    var items by mutableIntStateOf(69)

    var activeOrders by mutableIntStateOf(10)
    var completedOrders by mutableIntStateOf(10)
    var cancelledOrders by mutableIntStateOf(10)

    var revenueData by mutableStateOf(
        listOf(
            RevenueData("Day 1", 900f),
            RevenueData("Day 2", 1100f),
            RevenueData("Day 3", 1050f),
            RevenueData("Day 4", 800f),
            RevenueData("Day 5", 1050f),
            RevenueData("Day 6", 600f),
            RevenueData("Day 7", 1300f)
        )
    )

    var transactions by mutableStateOf(
        listOf(
            Transaction("Debmalya Sen", 6.69, "2024-01-08"),
            Transaction("Debmalya Sen", 6.69, "2024-01-08"),
            Transaction("Debmalya Sen", 6.69, "2024-01-08")
        )
    )

    var ordersSummaryTimeFilter by mutableStateOf(TimeFilter.MONTHLY)
    var revenueTimeFilter by mutableStateOf(TimeFilter.MONTHLY)

    // Function to update time filters
    fun updateOrdersSummaryTimeFilter(filter: TimeFilter) {
        ordersSummaryTimeFilter = filter
        // Here you would typically fetch new data based on the filter
    }

    fun updateRevenueTimeFilter(filter: TimeFilter) {
        revenueTimeFilter = filter
        // Here you would typically fetch new data based on the filter
    }
}