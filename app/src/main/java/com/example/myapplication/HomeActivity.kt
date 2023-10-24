package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.classes.Date
import com.example.myapplication.data.Account
import com.example.myapplication.database.SQLiteHelper


class HomeActivity : AppCompatActivity() {

    private lateinit var popupWindow: PopupWindow
    private lateinit var popupView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        updateDate()

        val button = findViewById<Button>(R.id.addNote)

        button.setOnClickListener {
            showPopup(button)
        }

    }

    private fun updateDate() {
        val date: Date = Date();
        val todayDateTextView: TextView = findViewById(R.id.todayDate)
        todayDateTextView.text = date.getTodayDate();
    }

    private fun showPopup(anchorView: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.popup_layout, null)

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        popupWindow = PopupWindow(popupView, width, height, true)

        val slideInAnimation = AnimationUtils.loadAnimation(this, R.drawable.popup_show_animation)
        popupView.startAnimation(slideInAnimation)

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)

        val closeButton =
            popupView.findViewById<Button>(R.id.addPassword)

        closeButton.setOnClickListener {
            addAccount();
            popupWindow.dismiss()
        }

    }



    private fun addAccount() {
        val accountNameField = findViewById<EditText>(R.id.accountName)
        val accountLoginField = findViewById<EditText>(R.id.accountLogin)
        val accountPasswordField = findViewById<EditText>(R.id.accountPassword)

        val accountName = accountNameField.text.toString()
        val accountLogin = accountLoginField.text.toString()
        val accountPassword = accountPasswordField.text.toString()
        val dbHelper = SQLiteHelper(this)

        if (accountName.isNotEmpty() && accountLogin.isNotEmpty() && accountPassword.isNotEmpty()) {

            val date = Date()
            val account = Account(1L, accountName, accountLogin, accountPassword, date.getCurrentDateTime(), 1L)

            dbHelper.addAccount(account)

            showToast("Konto zostało dodane.")
        } else {
            showToast("Wszystkie pola muszą być wypełnione.")
        }
    }

    private fun showToast(text: String) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

}