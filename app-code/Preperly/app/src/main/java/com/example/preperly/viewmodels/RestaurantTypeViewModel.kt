//package com.example.preperly.viewmodels
//
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateMapOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import java.text.SimpleDateFormat
//import java.util.Locale
//import java.util.regex.Pattern
//
//class RestaurantTypeViewModel {
//
//    var currentStep by mutableIntStateOf(2)
//    var cuisineType by mutableStateOf("")
//    var cuisineTypeError = mutableStateOf<String?>(null)
//        private set
//    var openTime by mutableStateOf("11:00 AM")
//    var closeTime by mutableStateOf("9:00 PM")
//    var openTimeAdv by mutableStateOf("11:00 AM")
//    var closeTimeAdv by mutableStateOf("9:00 PM")
//    private val timePattern = Pattern.compile("^(1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM)$")
//    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//
//    var errorMessageNormal by mutableStateOf("")
//    var errorMessageAdv by mutableStateOf("")
//
//    val selectedDays = mutableStateListOf<String>()
//
//    var selectedDayAdv by mutableStateOf("Select Day")
//
//    data class TimeSlot(var openTime: String, var closeTime: String)
//    // Stores multiple time slots per day
//    private val timeSlots = mutableStateMapOf<String, MutableList<TimeSlot>>()
//
//    fun readTimeSlot(){
//
//        timeSlots.forEach { (day, slots) ->
//            Log.d("TimeSlots", "Day: $day")
//            slots.forEach { slot ->
//                Log.d("TimeSlots", "  Open Time: ${slot.openTime}, Close Time: ${slot.closeTime}")
//            }
//        }
//    }
//
//    fun saveTimeSlot() {
//
//        isValidString(cuisineType)
//
//        // Create a temporary set to store the selected days
//        val currentSelectedDays = mutableSetOf<String>()
//        selectedDays.forEach { day ->
//            currentSelectedDays.add(day)
//        }
//        // Clear existing time slots for days that are no longer selected
//        timeSlots.keys.toList().forEach { day ->
//            if (!currentSelectedDays.contains(day)) {
//                timeSlots.remove(day)
//            }
//        }
//
//        selectedDays.forEach { day ->
//            val timeSlot = TimeSlot(openTime, closeTime)
//
//            if (!isValidTimeFormat(openTime) || !isValidTimeFormat(closeTime)) {
//                errorMessageNormal = "Invalid time format. Please use hh:mm AM/PM format."
//                Log.d("error Msg normal",errorMessageNormal)
//                return
//            }
//
//            if (!isOpenTimeBeforeCloseTime(openTime, closeTime)) {
//                errorMessageNormal = "Open time must be before close time."
//                Log.d("error Msg normal",errorMessageNormal)
//                return
//            }
//
//            if (isTimeSlotOverlap(day, timeSlot)) {
//                errorMessageNormal = "Time slot overlaps with an existing slot for $day."
//                Log.d("currentTimeSlots", "TimeSlots before adding: ${timeSlots[day]}")
//                Log.d("error Msg normal",errorMessageNormal)
//
//                return
//            }
//
////            Log.d("currentTimeSlots", "TimeSlots before adding: ${timeSlots[day]}")
//
//            errorMessageNormal = ""
//
//            if (timeSlots[day] == null) {
//                timeSlots[day] = mutableListOf()
//            }
//            if (timeSlots[day]?.contains(timeSlot) != true) {
//                timeSlots[day]?.add(timeSlot)
//                Log.d("currentTimeSlots", "Added new time slot: $timeSlot")
//            }else{
//                Log.d("currentTimeSlots", "Duplicate time slot found, not adding: $timeSlot")
//            }
//            Log.d("error Msg normal",errorMessageNormal)
//        }
//    }
//
//    fun saveAdvTimeSlot() {
//        val timeSlot = TimeSlot(openTimeAdv, closeTimeAdv)
//
//        if(selectedDayAdv != "Select Day"){
//
//            if (!isValidTimeFormat(openTimeAdv) || !isValidTimeFormat(closeTimeAdv)) {
//                errorMessageAdv = "Invalid time format. Please use hh:mm AM/PM format."
//                Log.d("error Msg Adv",errorMessageAdv)
//                return
//            }
//
//            if (!isOpenTimeBeforeCloseTime(openTimeAdv, closeTimeAdv)) {
//                errorMessageAdv = "Open time must be before close time."
//                Log.d("error Msg Adv",errorMessageAdv)
//                return
//            }
//
//            if (isTimeSlotOverlap(selectedDayAdv, timeSlot)) {
//                errorMessageAdv = "Time slot overlaps with an existing slot for $selectedDayAdv."
//                Log.d("error Msg Adv",errorMessageAdv)
//                return
//            }
//
//            errorMessageAdv = ""
//
//            if (timeSlots[selectedDayAdv] == null) {
//                timeSlots[selectedDayAdv] = mutableListOf()
//            }
//
//            if (timeSlots[selectedDayAdv]?.contains(timeSlot) != true) {
//                timeSlots[selectedDayAdv]?.add(timeSlot)
//            }
//
//            Log.d("error Msg Adv",errorMessageAdv)
//        }
//    }
//
//    private fun isValidTimeFormat(time: String): Boolean {
//        return timePattern.matcher(time).matches()
//    }
//
//    private fun isOpenTimeBeforeCloseTime(openTime: String, closeTime: String): Boolean {
//        val openDate = timeFormat.parse(openTime)
//        val closeDate = timeFormat.parse(closeTime)
//        if (openDate != null) {
//            return openDate.before(closeDate)
//        }
//        return false
//    }
//
//    private fun isTimeSlotOverlap(day: String, newSlot: TimeSlot): Boolean {
//        val existingSlots = timeSlots[day] ?: return false
//        val newOpen = timeFormat.parse(newSlot.openTime)
//        val newClose = timeFormat.parse(newSlot.closeTime)
//
//        for (slot in existingSlots) {
//            val existingOpen = timeFormat.parse(slot.openTime)
//            val existingClose = timeFormat.parse(slot.closeTime)
//
//            // Check if the new slot overlaps with any existing slot
//            if (newOpen != null && newClose != null) {
//
//                if (existingOpen != null && existingClose != null) {
//                    if(newOpen.time == existingOpen.time && newClose.time == existingClose.time){
//                        continue
//                    }
//                }
//                if (newOpen.before(existingClose) && newClose.after(existingOpen)) {
//                    return true
//                }
//
//            }
//        }
//        return false
//    }
//    private fun isValidString(name: String) {
//
//        val namePattern = Pattern.compile("^[a-zA-Z\\s'-]{2,50}$")
//        if(!namePattern.matcher(name).matches()){
//            cuisineTypeError.value = "The length must be 2-50 characters, and can only contain letters, spaces, ', or -."
//            Log.d("error cuisine",cuisineTypeError.value.toString())
//        }
//        else if(name.isEmpty()){
//            cuisineTypeError.value = "The cuisine type cannot be empty"
//            Log.d("error cuisine",cuisineTypeError.value.toString())
//        }
//        else{
//            cuisineTypeError.value = null
//            Log.d("error cuisine",cuisineTypeError.value.toString())
//        }
//
//    }
//}

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
