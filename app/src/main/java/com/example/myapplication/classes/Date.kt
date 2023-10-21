package com.example.myapplication.classes

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Date {

    fun getTodayDate(): String? {
        val today = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // You can change the format
        return today.format(formatter)
    }
}