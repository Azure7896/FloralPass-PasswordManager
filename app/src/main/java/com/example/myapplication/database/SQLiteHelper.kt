package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.data.Account
import com.example.myapplication.data.User
import java.util.Date

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MyDatabase.db"

        const val TABLE_USERS = "users"
        const val TABLE_ACCOUNTS = "accounts"

        const val KEY_ID = "id"

        const val KEY_USER_NAME = "name"
        const val KEY_USER_PASSWORD = "password"

        const val KEY_ACCOUNT_NAME = "name"
        const val KEY_ACCOUNT_LOGIN = "login"
        const val KEY_ACCOUNT_PASSWORD = "password"
        const val KEY_CREATED_DATE = "created_date"
        const val KEY_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Users table
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_USER_NAME TEXT,"
                + "$KEY_USER_PASSWORD TEXT)")

        db.execSQL(createUsersTable)

        // Create Accounts table
        val createAccountsTable = ("CREATE TABLE $TABLE_ACCOUNTS ("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_ACCOUNT_NAME TEXT,"
                + "$KEY_ACCOUNT_LOGIN TEXT,"
                + "$KEY_ACCOUNT_PASSWORD TEXT,"
                + "$KEY_CREATED_DATE TEXT,"
                + "$KEY_USER_ID INTEGER, "
                + "FOREIGN KEY($KEY_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")

        db.execSQL(createAccountsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")

        // Create new tables
        onCreate(db)
    }

    fun registerUser(user: User) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_USER_NAME, user.name)
        contentValues.put(KEY_USER_PASSWORD, user.password)
        db.insert(TABLE_USERS, null, contentValues)
        db.close()
    }

    fun isUserExists(userId: Long): Boolean {
        val db = this.readableDatabase
        val query = "SELECT $KEY_ID FROM $TABLE_USERS WHERE $KEY_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        val userExists = cursor.count > 0

        cursor.close()
        db.close()

        return userExists
    }

    fun getPasswordById(userId: Long): String? {
        val db = this.readableDatabase
        val query = "SELECT $KEY_ACCOUNT_PASSWORD FROM $TABLE_USERS WHERE $KEY_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        var password: String? = null

        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_PASSWORD))
        }

        cursor.close()
        db.close()

        return password
    }

    fun getUserNameById(userId: Long): String? {
        val db = this.readableDatabase
        val query = "SELECT $KEY_USER_NAME FROM $TABLE_USERS WHERE $KEY_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        var userName: String? = null

        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME))
        }

        cursor.close()
        db.close()

        return userName
    }

    fun addAccount(account: Account) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ACCOUNT_NAME, account.id)
        contentValues.put(KEY_ACCOUNT_NAME, account.name)
        contentValues.put(KEY_ACCOUNT_NAME, account.login)
        contentValues.put(KEY_ACCOUNT_PASSWORD, account.password)
        contentValues.put(KEY_CREATED_DATE, account.createdDate)
        contentValues.put(KEY_USER_ID, account.userId)

        db.insert(TABLE_ACCOUNTS, null, contentValues)
        db.close()
    }


}