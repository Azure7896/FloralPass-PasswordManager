package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication.data.User

import com.example.myapplication.database.SQLiteHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var repeatPasswordField: EditText
    private lateinit var registerButton: Button
    private val validation = Validation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setListeners()
    }

    private fun initializeViews() {
        nameField = findViewById(R.id.nameField)
        passwordField = findViewById(R.id.passwordField)
        repeatPasswordField = findViewById(R.id.repeatPasswordField)
        registerButton = findViewById(R.id.registerButton)
    }

    private fun setListeners() {
        registerButton.setOnClickListener {
            val passwordFieldString = passwordField.text.toString()
            val repeatPasswordFieldString = repeatPasswordField.text.toString()
            val nameFieldString = nameField.text.toString()

            when {
                validation.checkPasswordFieldLength(passwordFieldString) &&
                        validation.checkPasswordAreTheSame(passwordFieldString, repeatPasswordFieldString) -> {
                    registerUser(nameFieldString, passwordFieldString)
                    navigateToLogin()
                    showToast("Konto zostało zarejestrowane")
                }
                !validation.checkPasswordFieldLength(passwordFieldString) ->
                    showToast("Hasło jest zbyt krótkie (min. 8 znaków)")
                nameFieldString.isEmpty() -> showToast("Nazwa użytkownika jest pusta")
                else -> showToast("Hasła do siebie nie pasują")
            }
        }
    }

    private fun registerUser(name: String, password: String) {
        val dbHelper = SQLiteHelper(this)
        val user = User(1L, name, SHAHash.sha256(password))
        dbHelper.registerUser(user)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}