package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.database.SQLiteHelper

class LoginActivity : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private var isTimerRunning = false
    private var secondsRemaining = 30
    private var attempts = 3

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val dbHelper = SQLiteHelper(this)

        //Set user name in the welcome message
        val loginText: TextView = findViewById(R.id.loginText)
        val name: String? = dbHelper.getUserNameById(1L);
        loginText.text = "Witaj $name, zaloguj się";

        //Get elements by id
        val passwordField = findViewById<EditText>(R.id.loginPasswordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val loginBlockedInfo: TextView = findViewById(R.id.loginBlockedInfo)

        //Get user password from database
        val userPasswordToCheck = dbHelper.getPasswordById(1L)


        //When login button clicked
        loginButton.setOnClickListener {
            //Create validation object
            val validation = Validation()
            //Parse passworfField to string
            val passwordFieldString = passwordField.text.toString()
            //Validate passwords
            if (validation.checkPasswordAreTheSame(passwordFieldString, userPasswordToCheck.toString())) {
                //Open home window
                navigateToHome()
            } else {
                //Subtract attemp
                attempts--
                //Show info
                showToast("Błędne hasło, pozostało prób: $attempts")
                if (attempts == 0) {
                    //Show info
                    showToast("Logowanie chwilowo zablokowane.")
                    //Start blocked login timer
                    startTimer(loginBlockedInfo, loginButton)
                }
            }
        }
    }

    //Prevent use back button
    override fun onBackPressed() {}

    //Show toast notification
    private fun showToast(text: String) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }


    //Open home window
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        showToast("Zalogowano pomyślnie")
    }

    //Start timer
    private fun startTimer(loginBlockedInfo: TextView, button: Button) {
        if (!isTimerRunning) {
            timer = object : CountDownTimer(secondsRemaining * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    secondsRemaining = (millisUntilFinished / 1000).toInt()
                    loginBlockedInfo.text = "Logowanie zablokowane, pozostało $secondsRemaining sekund."
                }

                override fun onFinish() {
                    secondsRemaining = 30
                    stopTimer()
                    button.isEnabled = true
                    loginBlockedInfo.text = ""
                    attempts = 3
                }
            }
            button.isEnabled = false
            timer?.start()
            isTimerRunning = true
        }
    }

    //Stop timer
    private fun stopTimer() {
        if (isTimerRunning) {
            timer?.cancel()
            isTimerRunning = false
        }
    }

}
