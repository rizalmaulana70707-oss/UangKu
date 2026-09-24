package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Category
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [TransactionItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uangku_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.transactionDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: TransactionDao) {
            val cal = Calendar.getInstance()
            val now = cal.timeInMillis

            // Today
            cal.set(Calendar.HOUR_OF_DAY, 13)
            cal.set(Calendar.MINUTE, 15)
            val todayLunch = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 8)
            cal.set(Calendar.MINUTE, 30)
            val todayTransport = cal.timeInMillis

            // Yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            cal.set(Calendar.HOUR_OF_DAY, 19)
            val yesterdayDinner = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 11)
            val yesterdayFreelance = cal.timeInMillis

            // 3 days ago
            cal.add(Calendar.DAY_OF_YEAR, -2)
            cal.set(Calendar.HOUR_OF_DAY, 15)
            val threeDaysAgoShopping = cal.timeInMillis

            // 5 days ago
            cal.add(Calendar.DAY_OF_YEAR, -2)
            cal.set(Calendar.HOUR_OF_DAY, 10)
            val fiveDaysAgoBills = cal.timeInMillis

            // Beginning of current month
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 9)
            val monthStartSalary = cal.timeInMillis

            // 15 days ago investment
            cal.set(Calendar.DAY_OF_MONTH, 15)
            cal.set(Calendar.HOUR_OF_DAY, 14)
            val midMonthInvestment = cal.timeInMillis

            val sampleData = listOf(
                TransactionItem(
                    title = "Makan Siang Soto Betawi",
                    amount = 45000.0,
                    type = TransactionType.EXPENSE,
                    categoryId = "food",
                    categoryName = "Makan & Minum",
                    categoryIcon = "restaurant",
                    categoryColor = 0xFFF97316,
                    timestamp = todayLunch,
                    note = "Makan siang bersama rekan kerja"
                ),
                TransactionItem(
                    title = "Bensin Pertamax",
                    amount = 50000.0,
                    type = TransactionType.EXPENSE,
                    categoryId = "transport",
                    categoryName = "Transportasi",
                    categoryIcon = "commute",
                    categoryColor = 0xFF06B6D4,
                    timestamp = todayTransport,
                    note = "Isi full tank motor"
                ),
                TransactionItem(
                    title = "Bonus Project Web UangKu",
                    amount = 2500000.0,
                    type = TransactionType.INCOME,
                    categoryId = "freelance",
                    categoryName = "Freelance",
                    categoryIcon = "laptop_mac",
                    categoryColor = 0xFF3B82F6,
                    timestamp = yesterdayFreelance,
                    note = "Pembayaran milestone akhir klien"
                ),
                TransactionItem(
                    title = "Belanja Mingguan Sayur & Buah",
                    amount = 215000.0,
                    type = TransactionType.EXPENSE,
                    categoryId = "shopping",
                    categoryName = "Belanja",
                    categoryIcon = "shopping_bag",
                    categoryColor = 0xFF8B5CF6,
                    timestamp = yesterdayDinner,
                    note = "Superindo dekat rumah"
                ),
                TransactionItem(
                    title = "Beli Mouse & Keyboard Wireless",
                    amount = 380000.0,
                    type = TransactionType.EXPENSE,
                    categoryId = "shopping",
                    categoryName = "Belanja",
                    categoryIcon = "shopping_bag",
                    categoryColor = 0xFF8B5CF6,
                    timestamp = threeDaysAgoShopping,
                    note = "Upgrade peralatan kerja"
                ),
                TransactionItem(
                    title = "Listrik PLN & WiFi Indihome",
                    amount = 650000.0,
                    type = TransactionType.EXPENSE,
                    categoryId = "bills",
                    categoryName = "Tagihan",
                    categoryIcon = "receipt_long",
                    categoryColor = 0xFFEC4899,
                    timestamp = fiveDaysAgoBills,
                    note = "Tagihan rutin bulanan"
                ),
                TransactionItem(
                    title = "Dividen Reksadana Saham",
                    amount = 450000.0,
                    type = TransactionType.INCOME,
                    categoryId = "investment",
                    categoryName = "Investasi",
                    categoryIcon = "trending_up",
                    categoryColor = 0xFF6366F1,
                    timestamp = midMonthInvestment,
                    note = "Hasil dividen kuartal 3"
                ),
                TransactionItem(
                    title = "Gaji Pokok PT Teknologi Indonesia",
                    amount = 9500000.0,
                    type = TransactionType.INCOME,
                    categoryId = "salary",
                    categoryName = "Gaji Pokok",
                    categoryIcon = "account_balance_wallet",
                    categoryColor = 0xFF059669,
                    timestamp = monthStartSalary,
                    note = "Gaji bulan berjalan"
                )
            )

            dao.insertAll(sampleData)
        }
    }
}
