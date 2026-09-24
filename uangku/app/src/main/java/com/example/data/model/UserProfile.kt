package com.example.data.model

data class UserProfile(
    val name: String = "Rizal Maulana",
    val email: String = "rizalmaulana70707@gmail.com",
    val phone: String = "+62 812-3456-7890",
    val monthlyBudget: Double = 5000000.0,
    val isReminderEnabled: Boolean = true,
    val reminderTime: String = "20:00",
    val isDarkMode: Boolean = false
)
