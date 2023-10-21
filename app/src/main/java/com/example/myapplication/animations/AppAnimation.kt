package com.example.myapplication.animations

import android.animation.ObjectAnimator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class AppAnimation : AppCompatActivity(){

    private val duration: Long = 3000;

    fun showAppAnimation(appName: TextView, author: TextView, button: Button) {
        val appNameAnimator = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f)
        val authorAnimator = ObjectAnimator.ofFloat(author, "alpha", 0f, 1f)
        val buttonAnimator = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f)
        appNameAnimator.duration = duration
        authorAnimator.duration = duration
        buttonAnimator.duration = duration
        appNameAnimator.start()
        authorAnimator.start()
        buttonAnimator.start()
    }
}