package com.example.preperly.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.RestaurantTypeViewModel

@Composable
fun RestaurantTypeAndTimingsScreen(
    viewModel: RestaurantTypeViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {

    var isDialogVisible by remember { mutableStateOf(false) }
    val cuisineTypeError by viewModel.cuisineTypeError
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        ProgressIndicator(viewModel.currentStep)

        Text(
            "Restaurant Type and Timings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = viewModel.cuisineType,
            onValueChange = { viewModel.cuisineType = it },
            label = { Text("Cuisine type*") },
            isError = cuisineTypeError != null,
            placeholder = { Text("Best describe the food you serve") },
            modifier = Modifier.fillMaxWidth()
        )

        cuisineTypeError?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Timings*", fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = viewModel.openTime,
                onValueChange = { viewModel.openTime = it },
                label = { Text("Opens at") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = viewModel.closeTime,
                onValueChange = { viewModel.closeTime = it },
                label = { Text("Closes at") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Open Days", fontWeight = FontWeight.Bold)

        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        daysOfWeek.forEach { day ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = viewModel.selectedDays.contains(day),
                    onCheckedChange = {
                        if (it) viewModel.selectedDays.add(day) else viewModel.selectedDays.remove(day)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = myRed)
                )
                Text(day)
                Log.d("selected days",viewModel.selectedDays.toString())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                isDialogVisible = true
            },
            colors = ButtonDefaults.textButtonColors(contentColor = myRed),
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Apply Advance options")

            if(isDialogVisible){
                TimeSlotDialog(
                    viewModel =viewModel,
                    onDismiss = {isDialogVisible = false})
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.errorMessageNormal.value.isNotEmpty()) {
            Text(
                text = viewModel.errorMessageNormal.value,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

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
                    viewModel.saveTimeSlot()
                    if (viewModel.errorMessageAdv.value.isEmpty() && viewModel.errorMessageNormal.value.isEmpty() && cuisineTypeError.isNullOrEmpty() && viewModel.selectedDays.size > 1) {
                        viewModel.readTimeSlot()
                        onNext()
                    }
//                    onNext()
//                    Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = myRed),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotDialog(viewModel: RestaurantTypeViewModel, onDismiss: () -> Unit) {
    val daysOfWeek = listOf("Select Day","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Drop-down for weekday selection
                Text("Select Day", fontWeight = FontWeight.Bold)

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.selectedDayAdv,
                        onValueChange = {viewModel.selectedDayAdv = it},
                        readOnly = true,
                        label = { Text("Day") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        daysOfWeek.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    viewModel.selectedDayAdv = day
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Timings*", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = viewModel.openTimeAdv,
                        onValueChange = { viewModel.openTimeAdv = it },
                        label = { Text("Opens at") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = viewModel.closeTimeAdv,
                        onValueChange = { viewModel.closeTimeAdv = it },
                        label = { Text("Closes at") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to save time slot for the selected day
                Button(
                    onClick =
                    {
                        viewModel.saveAdvTimeSlot()
                        if (viewModel.errorMessageAdv.value.isEmpty() && viewModel.selectedDayAdv != "Select Day"){
                            Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show()
                            viewModel.readTimeSlot()
                        }

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = myRed)
                ) {
                    Text("Add Time Slot")
                }
                Button(
                    onClick = onDismiss, // Call onDismiss to hide dialog
                    colors = ButtonDefaults.buttonColors(containerColor = myRed),
                    modifier = Modifier.align(Alignment.End)

                ) {
                    Text("Close", color = Color.White)
                }

                if (viewModel.errorMessageAdv.value.isNotEmpty()) {
                    Text(
                        text = viewModel.errorMessageAdv.value,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestaurantTypePreview() {
    val navController = rememberNavController()
    MaterialTheme {
        RestaurantTypeAndTimingsScreen(
            viewModel = RestaurantTypeViewModel(),
            onNext = {navController.navigate("step3")},
            onBack = {navController.popBackStack()}
        )

    }
}