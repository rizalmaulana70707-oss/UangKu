package com.example.data.model

enum class TransactionType {
    INCOME,
    EXPENSE;

    fun getDisplayName(): String = when (this) {
        INCOME -> "Pemasukan"
        EXPENSE -> "Pengeluaran"
    }
}
