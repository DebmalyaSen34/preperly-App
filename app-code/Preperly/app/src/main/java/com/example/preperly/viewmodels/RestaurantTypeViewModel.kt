package com.example.preperly.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

class RestaurantTypeViewModel {

    var currentStep by mutableIntStateOf(2)
    var cuisineType by mutableStateOf("")
    var cuisineTypeError = mutableStateOf<String?>(null)
        private set
    var openTime by mutableStateOf("11:00 AM")
    var closeTime by mutableStateOf("9:00 PM")
    var openTimeAdv by mutableStateOf("11:00 AM")
    var closeTimeAdv by mutableStateOf("9:00 PM")
    private val timePattern = Pattern.compile("^(1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM)$")
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    var errorMessageNormal by mutableStateOf("")
    var errorMessageAdv by mutableStateOf("")

    val selectedDays = mutableStateListOf<String>()

    var selectedDayAdv by mutableStateOf("Select Day")

    data class TimeSlot(var openTime: String, var closeTime: String)
    // Stores multiple time slots per day
    private val timeSlots = mutableStateMapOf<String, MutableSet<TimeSlot>>()


    fun readTimeSlot(){

        Log.d("total time",timeSlots.toString())
        timeSlots.forEach { (day, slots) ->
            Log.d("TimeSlots", "Day: $day")
            slots.forEach { slot ->
                Log.d("TimeSlots", "  Open Time: ${slot.openTime}, Close Time: ${slot.closeTime}")
            }
        }
    }

    fun saveTimeSlot() {

        isValidString(cuisineType)

        selectedDays.forEach { day ->
            val timeSlot = TimeSlot(openTime, closeTime)

            if (!isValidTimeFormat(openTime) || !isValidTimeFormat(closeTime)) {
                errorMessageNormal = "Invalid time format. Please use hh:mm AM/PM format."
                Log.d("error Msg normal",errorMessageNormal)
                return
            }

            if (!isOpenTimeBeforeCloseTime(openTime, closeTime)) {
                errorMessageNormal = "Open time must be before close time."
                Log.d("error Msg normal",errorMessageNormal)
                return
            }

            if (isTimeSlotOverlap(day, timeSlot)) {
                errorMessageNormal = "Time slot overlaps with an existing slot for $day."
                Log.d("currentTimeSlots", "TimeSlots before adding: ${timeSlots[day]}")
                Log.d("error Msg normal",errorMessageNormal)

                return
            }

//            Log.d("currentTimeSlots", "TimeSlots before adding: ${timeSlots[day]}")

            errorMessageNormal = ""

            if (timeSlots[day] == null) {
                timeSlots[day] = mutableSetOf()
            }
            if (timeSlots[day]?.contains(timeSlot) != true) {
                timeSlots[day]?.add(timeSlot)
                Log.d("currentTimeSlots", "Added new time slot: $timeSlot")
            }else{
                Log.d("currentTimeSlots", "Duplicate time slot found, not adding: $timeSlot")
            }
            Log.d("error Msg normal",errorMessageNormal)
        }
    }

    fun saveAdvTimeSlot() {
        val timeSlot = TimeSlot(openTimeAdv, closeTimeAdv)

        if(selectedDayAdv != "Select Day"){

            if (!isValidTimeFormat(openTimeAdv) || !isValidTimeFormat(closeTimeAdv)) {
                errorMessageAdv = "Invalid time format. Please use hh:mm AM/PM format."
                Log.d("error Msg Adv",errorMessageAdv)
                return
            }

            if (!isOpenTimeBeforeCloseTime(openTimeAdv, closeTimeAdv)) {
                errorMessageAdv = "Open time must be before close time."
                Log.d("error Msg Adv",errorMessageAdv)
                return
            }

            if (isTimeSlotOverlap(selectedDayAdv, timeSlot)) {
                errorMessageAdv = "Time slot overlaps with an existing slot for $selectedDayAdv."
                Log.d("error Msg Adv",errorMessageAdv)
                return
            }

            errorMessageAdv = ""

            if (timeSlots[selectedDayAdv] == null) {
                timeSlots[selectedDayAdv] = mutableSetOf()
            }

            if (timeSlots[selectedDayAdv]?.contains(timeSlot) != true) {
                timeSlots[selectedDayAdv]?.add(timeSlot)
            }

            Log.d("error Msg Adv",errorMessageAdv)
        }
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return timePattern.matcher(time).matches()
    }

    private fun isOpenTimeBeforeCloseTime(openTime: String, closeTime: String): Boolean {
        val openDate = timeFormat.parse(openTime)
        val closeDate = timeFormat.parse(closeTime)
        if (openDate != null) {
            return openDate.before(closeDate)
        }
        return false
    }

    private fun isTimeSlotOverlap(day: String, newSlot: TimeSlot): Boolean {
        val existingSlots = timeSlots[day] ?: return false
        val newOpen = timeFormat.parse(newSlot.openTime)
        val newClose = timeFormat.parse(newSlot.closeTime)

        for (slot in existingSlots) {
            val existingOpen = timeFormat.parse(slot.openTime)
            val existingClose = timeFormat.parse(slot.closeTime)

            // Check if the new slot overlaps with any existing slot
            if (newOpen != null && newClose != null) {
                if (newOpen.before(existingClose) && newClose.after(existingOpen)) {
                    return true
                }
            }
        }
        return false
    }
    private fun isValidString(name: String) {

        val namePattern = Pattern.compile("^[a-zA-Z\\s'-]{2,50}$")
        if(!namePattern.matcher(name).matches()){
            cuisineTypeError.value = "The length must be 2-50 characters, and can only contain letters, spaces, ', or -."
            Log.d("error cuisine",cuisineTypeError.value.toString())
        }
        else if(name.isEmpty()){
            cuisineTypeError.value = "The cuisine type cannot be empty"
            Log.d("error cuisine",cuisineTypeError.value.toString())
        }
        else{
            cuisineTypeError.value = null
            Log.d("error cuisine",cuisineTypeError.value.toString())
        }

    }
}