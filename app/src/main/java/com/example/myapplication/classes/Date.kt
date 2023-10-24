package com.example.myapplication.classes

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class Date {

    fun getTodayDate(): String? {
        val today = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // You can change the format
        return today.format(formatter)
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}