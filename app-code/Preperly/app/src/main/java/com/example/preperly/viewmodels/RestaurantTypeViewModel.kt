
package com.example.preperly.viewmodels

import android.util.Log
import androidx.compose.runtime.*
import com.example.preperly.datamodels.DayTimeSlots
import com.example.preperly.datamodels.TimeSlot
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern


class RestaurantTypeViewModel {

    // Constants and Patterns
    private val timePattern = Pattern.compile("^(1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM)$")
    private val namePattern = Pattern.compile("^[a-zA-Z\\s'-]{2,50}$")
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // State Variables
    var currentStep by mutableIntStateOf(2)
    var cuisineType by mutableStateOf("")
    var cuisineTypeError = mutableStateOf<String?>(null)
        private set
    var openTime by mutableStateOf("11:00 AM")
    var closeTime by mutableStateOf("9:00 PM")
    var openTimeAdv by mutableStateOf("11:00 AM")
    var closeTimeAdv by mutableStateOf("9:00 PM")

    var errorMessageNormal = mutableStateOf("")
    var errorMessageAdv = mutableStateOf("")

    val selectedDays = mutableStateListOf<String>()

    var selectedDayAdv by mutableStateOf("Select Day")

    private val timeSlots = mutableStateMapOf<String, MutableList<TimeSlot>>()

    // Read Time Slots
    fun readTimeSlot() {
        Log.d("Selected Days",selectedDays.toString())
        timeSlots.forEach { (day, slots) ->
            Log.d("TimeSlots", "Day: $day")
            slots.forEach { slot ->
                Log.d("TimeSlots", "  Open: ${slot.openTime}, Close: ${slot.closeTime}")
            }
        }
        testDataToJson()
    }

    // Save Normal Time Slots
    fun saveTimeSlot() {
        if (!validateCuisineType()) return

        if(selectedDays.isEmpty()){
            errorMessageNormal.value = "Please select atleast 1 day "
        }
        // Update selected days
        updateSelectedDays()

        selectedDays.forEach { day ->
            val timeSlot = TimeSlot(openTime, closeTime)

            if (validateTimeSlot(openTime, closeTime, errorMessageNormal) && !isTimeSlotOverlap(day, timeSlot)) {
                addTimeSlot(day, timeSlot)
            }
        }
    }

    // Save Advanced Time Slot
    fun saveAdvTimeSlot() {
        if (selectedDayAdv == "Select Day") return

        val timeSlot = TimeSlot(openTimeAdv, closeTimeAdv)
        if (validateTimeSlot(openTimeAdv, closeTimeAdv, errorMessageAdv) && !isTimeSlotOverlap(selectedDayAdv, timeSlot)) {
            addTimeSlot(selectedDayAdv, timeSlot)
            if(!selectedDays.contains(selectedDayAdv)){
                selectedDays.add(selectedDayAdv)
            }

        }
    }

    // Validate Cuisine Type
    private fun validateCuisineType(): Boolean {
        return if (cuisineType.isEmpty()) {
            cuisineTypeError.value = "Cuisine type cannot be empty."
            false
        } else if (!namePattern.matcher(cuisineType).matches()) {
            cuisineTypeError.value = "Invalid cuisine type. Use 2-50 characters, letters, spaces, ', or -."
            false
        } else {
            cuisineTypeError.value = null
            true
        }
    }

    // Validate Time Slot
    private fun validateTimeSlot(openTime: String, closeTime: String, errorMessage: MutableState<String>): Boolean {
        return when {
            !isValidTimeFormat(openTime) || !isValidTimeFormat(closeTime) -> {
                errorMessage.value = "Invalid time format. Use hh:mm AM/PM."
                false
            }
            !isOpenTimeBeforeCloseTime(openTime, closeTime) -> {
                errorMessage.value = "Open time must be before close time."
                false
            }
            else -> {
                errorMessage.value = ""
                true
            }
        }
    }

    // Update Selected Days
    private fun updateSelectedDays() {
        val currentSelectedDays = selectedDays.toSet()
        timeSlots.keys.filterNot { currentSelectedDays.contains(it) }.forEach { timeSlots.remove(it) }
    }

    // Add Time Slot
    private fun addTimeSlot(day: String, timeSlot: TimeSlot) {
        if (timeSlots[day] == null) {
            timeSlots[day] = mutableListOf()
        }
        Log.d("TimeSlots before adding new",timeSlots.toString())

        if (!timeSlots[day]!!.contains(timeSlot)) {
            timeSlots[day]!!.add(timeSlot)
            Log.d("TimeSlots", "Added slot: $timeSlot for $day")
        }

    }

    // Check Time Slot Overlap
    private fun isTimeSlotOverlap(day: String, newSlot: TimeSlot): Boolean {
        val existingSlots = timeSlots[day] ?: return false
        val newOpen = timeFormat.parse(newSlot.openTime)
        val newClose = timeFormat.parse(newSlot.closeTime)

        return existingSlots.any { slot ->
            val existingOpen = timeFormat.parse(slot.openTime)
            val existingClose = timeFormat.parse(slot.closeTime)
            newOpen?.before(existingClose) == true && newClose?.after(existingOpen) == true
        }
    }

    // Helper Methods
    private fun isValidTimeFormat(time: String): Boolean = timePattern.matcher(time).matches()
    private fun isOpenTimeBeforeCloseTime(openTime: String, closeTime: String): Boolean {
        val openDate = timeFormat.parse(openTime)
        val closeDate = timeFormat.parse(closeTime)
        return openDate?.before(closeDate) == true
    }

    private fun groupTimeSlotsByDay(): List<DayTimeSlots> {
        return timeSlots.map { (day, slots) ->
            DayTimeSlots(
                day = day,
                slots = slots.toList() // Convert MutableSet to List
            )
        }
    }

    private fun testDataToJson(){
        val groupedTimeSlots = groupTimeSlotsByDay()
        Log.d("beforeJson", groupedTimeSlots.toString())
        Log.d("GroupedTimeSlots",Gson().toJson(groupedTimeSlots))
    }

}
