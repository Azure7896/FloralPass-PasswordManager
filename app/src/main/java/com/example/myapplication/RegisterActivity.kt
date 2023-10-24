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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameField = findViewById<EditText>(R.id.nameField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val repeatPasswordField = findViewById<EditText>(R.id.repeatPasswordField)

        val button = findViewById<Button>(R.id.registerButton)

        val validation = Validation()

        button.setOnClickListener {
            val passwordFieldString = passwordField.text.toString()
            val repeatPasswordFieldString = repeatPasswordField.text.toString()
            val nameFieldString = nameField.text.toString()

            if (validation.checkPasswordFieldLength(passwordFieldString) && validation.checkPasswordAreTheSame(
                    passwordFieldString,
                    repeatPasswordFieldString
                )
            ) {
                registerUser(nameFieldString, passwordFieldString)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                showToast("Konto zostało zarejestrowane")
            } else {
                if (!validation.checkPasswordFieldLength(passwordFieldString)) {
                    showToast("Hasło jest zbyt krótkie (min. 8 znaków)")
                }
                if (nameFieldString.isEmpty()) {
                    showToast("Nazwa użytkownika jest pusta")
                }
                if (!validation.checkPasswordAreTheSame(
                        passwordFieldString,
                        repeatPasswordFieldString
                    )
                ) {
                    showToast("Hasła do siebie nie pasują")
                }
            }
        }

    }


    private fun registerUser(name: String, password: String) {

        val dbHelper = SQLiteHelper(this)
        val user = User(20, name, password)
        dbHelper.registerUser(user)
    }

   private fun showToast(text: String) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
         toast.show()
    }

}