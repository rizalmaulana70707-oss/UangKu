package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Category
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.model.UserProfile
import com.example.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val netDifference: Double = 0.0
)

data class DaySummary(
    val dateMillis: Long,
    val dayLabel: String,
    val income: Double,
    val expense: Double,
    val transactions: List<TransactionItem>
)

data class WeekSummary(
    val weekLabel: String,
    val startMillis: Long,
    val endMillis: Long,
    val totalIncome: Double,
    val totalExpense: Double,
    val dailyBreakdowns: List<DaySummary>
)

data class MonthSummary(
    val monthLabel: String,
    val year: Int,
    val month: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val weeklyBreakdowns: List<WeekSummary>
)

class MainViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    // User authentication state
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // User profile state
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // All transactions from Room DB
    val allTransactions: StateFlow<List<TransactionItem>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Summary calculations
    val summary: StateFlow<FinancialSummary> = allTransactions.combine(_userProfile) { list, _ ->
        var income = 0.0
        var expense = 0.0
        for (item in list) {
            if (item.type == TransactionType.INCOME) {
                income += item.amount
            } else {
                expense += item.amount
            }
        }
        val bal = income - expense
        FinancialSummary(
            totalIncome = income,
            totalExpense = expense,
            balance = bal,
            netDifference = income - expense
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialSummary()
    )

    // Search and filter for History screen
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<TransactionType?>(null)
    val selectedTypeFilter: StateFlow<TransactionType?> = _selectedTypeFilter.asStateFlow()

    // Filtered transactions for History screen
    val filteredTransactions: StateFlow<List<TransactionItem>> = combine(
        allTransactions,
        _searchQuery,
        _selectedTypeFilter
    ) { list, query, typeFilter ->
        list.filter { item ->
            val matchesType = typeFilter == null || item.type == typeFilter
            val matchesQuery = query.isBlank() || 
                item.title.contains(query, ignoreCase = true) ||
                item.categoryName.contains(query, ignoreCase = true) ||
                item.note.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Selected Date for Daily Report (defaults to today)
    private val _selectedDailyDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDailyDate: StateFlow<Long> = _selectedDailyDate.asStateFlow()

    // Selected Month & Year for Monthly Report
    private val _selectedMonthCal = MutableStateFlow(Calendar.getInstance())
    val selectedMonthCal: StateFlow<Calendar> = _selectedMonthCal.asStateFlow()

    // Selected Week offset (0 = current week, -1 = last week, etc.)
    private val _selectedWeekOffset = MutableStateFlow(0)
    val selectedWeekOffset: StateFlow<Int> = _selectedWeekOffset.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    // UI actions
    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

    fun login(emailOrPhone: String, password: String):Boolean {
        // Simple authentication validation
        if (emailOrPhone.isNotBlank() && password.isNotBlank()) {
            val name = if (emailOrPhone.contains("@")) {
                emailOrPhone.substringBefore("@").replace(".", " ").capitalizeWords()
            } else "Rizal Maulana"
            _userProfile.value = _userProfile.value.copy(
                name = if (name.isNotBlank()) name else "Rizal Maulana",
                email = if (emailOrPhone.contains("@")) emailOrPhone else "rizalmaulana70707@gmail.com"
            )
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun updateProfile(name: String, email: String, monthlyBudget: Double, reminder: Boolean, reminderTime: String) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            email = email,
            monthlyBudget = monthlyBudget,
            isReminderEnabled = reminder,
            reminderTime = reminderTime
        )
    }

    fun toggleDarkMode() {
        _userProfile.value = _userProfile.value.copy(
            isDarkMode = !_userProfile.value.isDarkMode
        )
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(type: TransactionType?) {
        _selectedTypeFilter.value = type
    }

    fun setSelectedDailyDate(dateMillis: Long) {
        _selectedDailyDate.value = dateMillis
    }

    fun setSelectedMonthOffset(offsetMonth: Int) {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _selectedMonthCal.value.timeInMillis
            add(Calendar.MONTH, offsetMonth)
        }
        _selectedMonthCal.value = newCal
    }

    fun setSelectedWeekOffset(offset: Int) {
        _selectedWeekOffset.value = offset
    }

    // Transaction CRUD
    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: Category,
        timestamp: Long,
        note: String = ""
    ) {
        viewModelScope.launch {
            val item = TransactionItem(
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
            repository.insert(item)
        }
    }

    fun updateTransaction(item: TransactionItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun deleteTransaction(item: TransactionItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun deleteTransactionById(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
