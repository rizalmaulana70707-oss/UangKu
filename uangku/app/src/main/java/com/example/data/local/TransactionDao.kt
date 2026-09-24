package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionItem>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getTransactionById(id: Long): Flow<TransactionItem?>

    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionItem>>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(item: TransactionItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TransactionItem>)

    @Update
    suspend fun updateTransaction(item: TransactionItem)

    @Delete
    suspend fun deleteTransaction(item: TransactionItem)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
