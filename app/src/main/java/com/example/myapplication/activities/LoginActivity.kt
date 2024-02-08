package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.myapplication.database.SQLiteHelper

class LoginActivity : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private var isTimerRunning = false
    private var secondsRemaining = 30
    private var attempts = 3

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showNotification(this)

        val dbHelper = SQLiteHelper(this)


        val loginText: TextView = findViewById(R.id.loginText)
        val name: String? = dbHelper.getUserNameById(1L);
        loginText.text = "Witaj $name, zaloguj się";


        val passwordField = findViewById<EditText>(R.id.loginPasswordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val loginBlockedInfo: TextView = findViewById(R.id.loginBlockedInfo)


        val userPasswordToCheck = dbHelper.getPasswordById(1L)


        loginButton.setOnClickListener {

            val validation = Validation()

            val passwordFieldString = passwordField.text.toString()

            if (validation.checkPasswordAreTheSame(SHAHash.sha256(passwordFieldString), userPasswordToCheck.toString())) {

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

    override fun onBackPressed() {}

    private fun showToast(text: String) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }


    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        showToast("Zalogowano pomyślnie")
    }

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

    private fun stopTimer() {
        if (isTimerRunning) {
            timer?.cancel()
            isTimerRunning = false
        }
    }

    fun showNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel("info_channel", "info", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, "info_channel")
            .setContentTitle("Uwaga!")
            .setContentText("Nie podawaj nikomu swojego hasła oraz innych prywatnych danych!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)

        notificationManager.notify(1, builder.build())
    }

}
