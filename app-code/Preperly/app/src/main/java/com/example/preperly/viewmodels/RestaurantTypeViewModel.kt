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
    var openTime by mutableStateOf("11:00 AM")
    var closeTime by mutableStateOf("9:00 PM")
    var openTimeAdv by mutableStateOf("11:00 AM")
    var closeTimeAdv by mutableStateOf("9:00 PM")
    private val timePattern = Pattern.compile("^(1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM)$")
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    var errorMessage by mutableStateOf("")

    val selectedDays = mutableStateListOf<String>()

    var selectedDayAdv by mutableStateOf("Monday")

    data class TimeSlot(var openTime: String, var closeTime: String)
    // Stores multiple time slots per day
    private val timeSlots = mutableStateMapOf<String, MutableSet<TimeSlot>>()


    fun readTimeSlot(){
        timeSlots.forEach { (day, slots) ->
            Log.d("TimeSlots", "Day: $day")
            slots.forEach { slot ->
                Log.d("TimeSlots", "  Open Time: ${slot.openTime}, Close Time: ${slot.closeTime}")
            }
        }
    }

//    fun saveAdvTimeSlot(){
//
//        if (timeSlots[selectedDayAdv] == null) {
//            timeSlots[selectedDayAdv] = mutableSetOf()
//        }
//
//        timeSlots[selectedDayAdv]?.add(
//            TimeSlot(
//                openTimeAdv,
//                closeTimeAdv
//            )
//        )
//    }
//    fun saveTimeSlot(){
//
//        selectedDays.forEach { day ->
//
//            if (timeSlots[day] == null) {
//                timeSlots[day] = mutableSetOf()
//            }
//            timeSlots[day]?.add(
//                TimeSlot(
//                    openTime,
//                    closeTime
//                )
//            )
//        }
//
//    }

    fun saveTimeSlot() {

        selectedDays.forEach { day ->
            val timeSlot = TimeSlot(openTime, closeTime)

            if (!isValidTimeFormat(openTime) || !isValidTimeFormat(closeTime)) {
                errorMessage = "Invalid time format. Please use hh:mm AM/PM format."
                return
            }

            if (!isOpenTimeBeforeCloseTime(openTime, closeTime)) {
                errorMessage = "Open time must be before close time."
                return
            }

            if (isTimeSlotOverlap(day, timeSlot)) {
                errorMessage = "Time slot overlaps with an existing slot for $day."
                return
            }
            errorMessage = ""

            if (timeSlots[day] == null) {
                timeSlots[day] = mutableSetOf()
            }
            timeSlots[day]?.add(timeSlot)
        }
    }

    fun saveAdvTimeSlot() {
        val timeSlot = TimeSlot(openTimeAdv, closeTimeAdv)

        if (!isValidTimeFormat(openTimeAdv) || !isValidTimeFormat(closeTimeAdv)) {
            errorMessage = "Invalid time format. Please use hh:mm AM/PM format."
            return
        }

        if (!isOpenTimeBeforeCloseTime(openTimeAdv, closeTimeAdv)) {
            errorMessage = "Open time must be before close time."
            return
        }

        if (isTimeSlotOverlap(selectedDayAdv, timeSlot)) {
            errorMessage = "Time slot overlaps with an existing slot for $selectedDayAdv."
            return
        }

        errorMessage = ""
        if (timeSlots[selectedDayAdv] == null) {
            timeSlots[selectedDayAdv] = mutableSetOf()
        }
        timeSlots[selectedDayAdv]?.add(timeSlot)
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
}