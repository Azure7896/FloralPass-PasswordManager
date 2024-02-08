package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.MyAdapter
import com.example.myapplication.classes.Date
import com.example.myapplication.data.Account
import com.example.myapplication.database.SQLiteHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class HomeActivity : AppCompatActivity() {

    private lateinit var popupWindow: PopupWindow
    private lateinit var popupView: View
    private var sort = "Sortowanie: Po najstarszych"
    private var sortCategoryNumber = 1
    private lateinit var accounts: List<Account>
    private lateinit var recyclerView: RecyclerView
    private lateinit var sortTextView: TextView
    private var sortState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        updateDate()

        val dbHelper = SQLiteHelper(this)
        accounts = dbHelper.getAllAccountsByNewest()

        fetchData()

        val button = findViewById<Button>(R.id.addNote)

        button.setOnClickListener {
            showPopup(button)
        }

        sortTextView = findViewById<TextView>(R.id.sort)

        sortTextView.setOnClickListener {
            toggleSort()
        }

//        checkAndNotifyThirtyDays(accounts, this)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                checkAndNotifyThirtyDays(accounts, this)
            },
            15000 // value in milliseconds
        )
    }


    private fun fetchData() {
        val dbHelper = SQLiteHelper(this)
        accounts = dbHelper.getAllAccountsByNewest()
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(applicationContext, accounts)
    }


    private fun toggleSort() {
        val dbHelper = SQLiteHelper(this)
        when (sortState) {
            0 -> {
                sortTextView.text = "Sortowanie: Po Najstarszych"
                accounts = dbHelper.getAllAccountsByNewest()
                sortState = 1
            }
            1 -> {
                sortTextView.text = "Sortowanie: Alfabetycznie"
                accounts = dbHelper.getAllAccountAlphabetically()
                sortState = 2
            }
            2 -> {
                sortTextView.text = "Sortowanie: Po Najnowszych"
                accounts = dbHelper.getAllAccounts()
                sortState = 0
            }
        }
        recyclerView.adapter = MyAdapter(applicationContext, accounts)
    }





    private fun updateDate() {
        val date: Date = Date()
        val todayDateTextView: TextView = findViewById(R.id.todayDate)
        todayDateTextView.text = date.getTodayDate()
    }


    private fun showPopup(anchorView: View) {
        setupPopupLayout()
        setupPopupWindow(anchorView)
        setupAnimation()
        setupPopupActions()
    }

    private fun setupPopupLayout() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.popup_layout, null)
    }

    private fun setupPopupWindow(anchorView: View) {
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }

    private fun setupAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.drawable.popup_show_animation)
        popupView.startAnimation(slideInAnimation)
    }

    private fun setupPopupActions() {
        val addAccount = popupView.findViewById<Button>(R.id.addAccount)
        addAccount.setOnClickListener {
            addAccount()
            popupWindow.dismiss()
        }
    }


    private fun addAccount() {
        val accountName = getAccountName()
        val accountLogin = getAccountLogin()
        val accountPassword = getAccountPassword()
        val dbHelper = SQLiteHelper(this)

        if (fieldsAreNotEmpty(accountName, accountLogin, accountPassword)) {
            val formattedDateTime = getCurrentFormattedDateTime()
            val encryptedPassword = encryptPassword(accountPassword)
            val account =
                createAccount(accountName, accountLogin, encryptedPassword, formattedDateTime)

            dbHelper.addAccount(account)
            showToast("Konto zostało dodane.")
            fetchData()
        } else {
            showToast("Wszystkie pola muszą być wypełnione.")
        }
    }

    private fun getAccountName(): String {
        val accountNameField = popupView.findViewById<EditText>(R.id.accountName)
        return accountNameField.text.toString()
    }

    private fun getAccountLogin(): String {
        val accountLoginField = popupView.findViewById<EditText>(R.id.accountLogin)
        return accountLoginField.text.toString()
    }

    private fun getAccountPassword(): String {
        val accountPasswordField = popupView.findViewById<EditText>(R.id.accountPassword)
        return accountPasswordField.text.toString()
    }

    private fun fieldsAreNotEmpty(
        accountName: String,
        accountLogin: String,
        accountPassword: String
    ): Boolean {
        return accountName.isNotEmpty() && accountLogin.isNotEmpty() && accountPassword.isNotEmpty()
    }

    private fun getCurrentFormattedDateTime(): String {
        val todayDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return todayDateTime.format(formatter)
    }

    private fun encryptPassword(password: String): String {
        return AESCrypt.encrypt(password)
    }

    private fun createAccount(
        name: String,
        login: String,
        password: String,
        dateTime: String
    ): Account {
        return Account(1L, name, login, password, dateTime, 1L)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun checkAndNotifyThirtyDays(records: List<Account>, context: Context) {
            val currentDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

            for (record in records) {
                val recordDateTime = LocalDateTime.parse(record.createdDate, formatter)
                val minutesDifference = ChronoUnit.MINUTES.between(recordDateTime, currentDate)

                if (minutesDifference >= 30 * 24 * 60) {
                    show30DaysNotification(context)
                    break
                }
            }
        }

        private fun show30DaysNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "30_day_channel",
                "30 Day Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(context, "30_day_channel")
                .setContentTitle("Uwaga!")
                .setContentText("Minęło 30 dni od wprowadzenia rekordu!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)

            notificationManager.notify(1, builder.build())
        }
    }

}