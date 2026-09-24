package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.repository.TransactionRepository
import com.example.ui.screens.AddEditTransactionScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainContainerScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.UangKuTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val scope = rememberCoroutineScope()
            val database = remember { AppDatabase.getDatabase(applicationContext, scope) }
            val repository = remember { TransactionRepository(database.transactionDao()) }
            val factory = remember { MainViewModelFactory(repository) }
            val viewModel: MainViewModel = viewModel(factory = factory)

            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
            val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
            val summary by viewModel.summary.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
            val selectedDailyDate by viewModel.selectedDailyDate.collectAsStateWithLifecycle()

            val navController = rememberNavController()

            // Track transaction to be edited
            var transactionToEdit by remember { mutableStateOf<TransactionItem?>(null) }
            var addTransactionInitialType by remember { mutableStateOf(TransactionType.EXPENSE) }

            UangKuTheme(darkTheme = userProfile.isDarkMode) {
                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Splash Screen
                    composable("splash") {
                        SplashScreen(
                            onStartClick = {
                                navController.navigate("main") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            },
                            onLoginClick = {
                                navController.navigate("login")
                            }
                        )
                    }

                    // 2. Login Screen
                    composable("login") {
                        LoginScreen(
                            onBackClick = { navController.popBackStack() },
                            onLoginSuccess = { email, name ->
                                viewModel.login(email, "password")
                                navController.navigate("main") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Main Container (Dashboard, Transaksi, Laporan, Grafik, Profil)
                    composable("main") {
                        MainContainerScreen(
                            userProfile = userProfile,
                            summary = summary,
                            transactions = transactions,
                            filteredTransactions = filteredTransactions,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedTypeFilter = selectedTypeFilter,
                            onTypeFilterChange = { viewModel.setTypeFilter(it) },
                            selectedDailyDate = selectedDailyDate,
                            onDailyDateChange = { viewModel.setSelectedDailyDate(it) },
                            onAddTransactionClick = { type ->
                                transactionToEdit = null
                                addTransactionInitialType = type ?: TransactionType.EXPENSE
                                navController.navigate("add_transaction")
                            },
                            onEditTransaction = { transaction ->
                                transactionToEdit = transaction
                                addTransactionInitialType = transaction.type
                                navController.navigate("add_transaction")
                            },
                            onDeleteTransaction = { transaction ->
                                viewModel.deleteTransaction(transaction)
                            },
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onUpdateProfile = { name, email, budget, reminder, reminderTime ->
                                viewModel.updateProfile(name, email, budget, reminder, reminderTime)
                            },
                            onResetDemoData = { viewModel.resetDemoData() },
                            onLogout = {
                                viewModel.logout()
                                navController.navigate("splash") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 4. Tambah / Edit Transaksi
                    composable("add_transaction") {
                        AddEditTransactionScreen(
                            initialType = addTransactionInitialType,
                            existingTransaction = transactionToEdit,
                            onBackClick = { navController.popBackStack() },
                            onSaveTransaction = { id, title, amount, type, category, timestamp, note ->
                                if (id != null) {
                                    viewModel.updateTransaction(
                                        TransactionItem(
                                            id = id,
                                            title = title,
                                            amount = amount,
                                            type = type,
                                            categoryId = category.id,
                                            categoryName = category.name,
                                            categoryIcon = category.iconKey,
                                            categoryColor = category.colorValue,
                                            timestamp = timestamp,
                                            note = note
                                        )
                                    )
                                } else {
                                    viewModel.addTransaction(
                                        title = title,
                                        amount = amount,
                                        type = type,
                                        category = category,
                                        timestamp = timestamp,
                                        note = note
                                    )
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
