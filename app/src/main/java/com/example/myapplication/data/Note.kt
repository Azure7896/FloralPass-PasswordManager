package com.example.myapplication.data

import java.util.Date

data class Note(
    val id: Int,
    val name: String,
    val createdDate: Date,
    val priority: Boolean
)
