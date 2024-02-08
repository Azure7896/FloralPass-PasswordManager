package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.example.myapplication.data.Account
import com.example.myapplication.database.SQLiteHelper
import java.time.LocalDateTime

class AccountActivity : AppCompatActivity() {

    private lateinit var popupWindow: PopupWindow
    private lateinit var popupView: View
    private lateinit var passwordButton: Button
    private lateinit var deleteButton: Button
    private lateinit var editAccountButton: Button
    private var account: Account? = null
    private var longPressDetected = false
    val dbHelper = SQLiteHelper(this)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val accountId = intent.getLongExtra("account_id", -1)
        account = dbHelper.getAccountById(accountId)

        val accountName: TextView = findViewById(R.id.accountNameText)
        accountName.text = account?.name

        val accountLogin: TextView = findViewById(R.id.accountLoginText)
        accountLogin.text = account?.login

        val accountPassword: TextView = findViewById(R.id.accountPasswordText)

        passwordButton = findViewById(R.id.accountPasswordButton)

        deleteButton = findViewById(R.id.deleteAccountButton)

        editAccountButton = findViewById<Button>(R.id.editAccountButton)

        passwordButton.setOnLongClickListener {
            longPressDetected = true
            accountPassword.text = AESCrypt.decrypt(account?.password)
            showToast("Hasło zostało ujawnione!")
            passwordButton.isEnabled = false
            changeButtonOpacity()
            true
        }

        editAccountButton.setOnClickListener {
            showPopup(editAccountButton)
        }

        deleteButton.setOnLongClickListener {
            showToast("Konto zostało usunięte.")
            deleteAccount()
            true
        }
    }

    private fun changeButtonOpacity() {
        val view = findViewById<View>(R.id.accountPasswordButton)
        view.alpha = 0.5f
    }

    private fun showToast(text: String) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    private fun showPopup(anchorView: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.popup_layout, null)

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        popupWindow = PopupWindow(popupView, width, height, true)

        val slideInAnimation = AnimationUtils.loadAnimation(this, R.drawable.popup_show_animation)
        popupView.startAnimation(slideInAnimation)

        val nameText = popupView.findViewById<EditText>(R.id.accountName)
        nameText.setText(account?.name)

        val loginText = popupView.findViewById<EditText>(R.id.accountLogin)
        loginText.setText(account?.login)

        val passwordText = popupView.findViewById<EditText>(R.id.accountPassword)
        passwordText.setText(AESCrypt.decrypt(account?.password))

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)

        val addAccount = popupView.findViewById<Button>(R.id.addAccount)


        addAccount.setOnClickListener {
            updateAccount()
            popupWindow.dismiss()
            finish()
            startActivity(intent)
        }
    }


    private fun updateAccount() {
        val accountNameField = popupView.findViewById<EditText>(R.id.accountName)
        val accountLoginField = popupView.findViewById<EditText>(R.id.accountLogin)
        val accountPasswordField = popupView.findViewById<EditText>(R.id.accountPassword)

        val accountName = accountNameField.text.toString()
        val accountLogin = accountLoginField.text.toString()
        val accountPassword = accountPasswordField.text.toString()

        if (accountName.isNotEmpty() && accountLogin.isNotEmpty() && accountPassword.isNotEmpty()) {

            var updatedAccount: Account? = account?.copy() // Copying the 'account' object

            updatedAccount?.name = accountName
            updatedAccount?.login = accountLogin
            updatedAccount?.password = AESCrypt.encrypt(accountPassword)
            dbHelper.updateAccount(updatedAccount)

            showToast("Konto zostało dodane.")
        } else {
            showToast("Wszystkie pola muszą być wypełnione.")
        }
    }

    private fun deleteAccount() {
        dbHelper.deleteAccount(account?.id)
        finish()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }


}