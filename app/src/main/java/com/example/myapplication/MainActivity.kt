package com.example.myapplication

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.animations.AppAnimation
import com.example.myapplication.classes.Date

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appAnimation = AppAnimation()

        val appNameTextView: TextView = findViewById(R.id.notes);
        val authorTextView: TextView = findViewById(R.id.author);
        val addNoteButton: Button = findViewById(R.id.addNote);
        appAnimation.showAppAnimation(appNameTextView, authorTextView, addNoteButton)

        val date: Date = Date();
        val todayDateTextView: TextView = findViewById(R.id.todayDate)

        todayDateTextView.text = date.getTodayDate();
    }
}