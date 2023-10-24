package com.example.myapplication

class Validation {
    fun checkPasswordFieldLength(password: String): Boolean {
        return password.length >= 8;
    }

    fun checkPasswordAreTheSame(password: String, repeatedPassword: String): Boolean {
        return password.equals(repeatedPassword);
    }

}