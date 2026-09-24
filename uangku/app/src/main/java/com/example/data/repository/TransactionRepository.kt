package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.TransactionDao
import com.example.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<TransactionItem>> = transactionDao.getAllTransactions()

    fun getTransactionById(id: Long): Flow<TransactionItem?> {
        return transactionDao.getTransactionById(id)
    }

    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionItem>> {
        return transactionDao.getTransactionsBetween(startTime, endTime)
    }

    suspend fun insert(transaction: TransactionItem): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun update(transaction: TransactionItem) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun delete(transaction: TransactionItem) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun checkAndSeedInitialData() {
        val count = transactionDao.getTransactionCount()
        if (count == 0) {
            AppDatabase.populateInitialData(transactionDao)
        }
    }

    suspend fun resetDemoData() {
        transactionDao.clearAll()
        AppDatabase.populateInitialData(transactionDao)
    }

    suspend fun clearAll() {
        transactionDao.clearAll()
    }
}
