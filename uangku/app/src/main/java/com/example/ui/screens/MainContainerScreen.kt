package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.model.UserProfile
import com.example.ui.theme.BluePrimary
import com.example.ui.viewmodel.FinancialSummary

sealed class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    object Beranda : BottomNavItem("Beranda", Icons.Filled.Home, Icons.Outlined.Home, "nav_beranda")
    object Transaksi : BottomNavItem("Transaksi", Icons.Filled.Receipt, Icons.Outlined.Receipt, "nav_transaksi")
    object Laporan : BottomNavItem("Laporan", Icons.Filled.Assessment, Icons.Outlined.Assessment, "nav_laporan")
    object Grafik : BottomNavItem("Grafik", Icons.Filled.ShowChart, Icons.Outlined.ShowChart, "nav_grafik")
    object Profil : BottomNavItem("Profil", Icons.Filled.Person, Icons.Outlined.Person, "nav_profil")
}

@Composable
fun MainContainerScreen(
    userProfile: UserProfile,
    summary: FinancialSummary,
    transactions: List<TransactionItem>,
    filteredTransactions: List<TransactionItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedTypeFilter: TransactionType?,
    onTypeFilterChange: (TransactionType?) -> Unit,
    selectedDailyDate: Long,
    onDailyDateChange: (Long) -> Unit,
    onAddTransactionClick: (type: TransactionType?) -> Unit,
    onEditTransaction: (TransactionItem) -> Unit,
    onDeleteTransaction: (TransactionItem) -> Unit,
    onToggleDarkMode: () -> Unit,
    onUpdateProfile: (name: String, email: String, monthlyBudget: Double, reminder: Boolean, reminderTime: String) -> Unit,
    onResetDemoData: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val navItems = listOf(
        BottomNavItem.Beranda,
        BottomNavItem.Transaksi,
        BottomNavItem.Laporan,
        BottomNavItem.Grafik,
        BottomNavItem.Profil
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BluePrimary,
                            selectedTextColor = BluePrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = BluePrimary.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        },
        floatingActionButton = {
            // Show FAB on Beranda or Transaksi screen
            if (selectedIndex == 0 || selectedIndex == 1) {
                FloatingActionButton(
                    onClick = { onAddTransactionClick(null) },
                    containerColor = BluePrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .testTag("fab_add_transaction")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Transaksi",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                0 -> DashboardScreen(
                    userProfile = userProfile,
                    summary = summary,
                    transactions = transactions,
                    onAddTransactionClick = onAddTransactionClick,
                    onViewAllTransactionsClick = { selectedIndex = 1 },
                    onTransactionClick = onEditTransaction
                )
                1 -> TransactionsScreen(
                    transactions = filteredTransactions,
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    selectedTypeFilter = selectedTypeFilter,
                    onTypeFilterChange = onTypeFilterChange,
                    onEditTransaction = onEditTransaction,
                    onDeleteTransaction = onDeleteTransaction
                )
                2 -> ReportsScreen(
                    transactions = transactions,
                    selectedDailyDate = selectedDailyDate,
                    onDailyDateChange = onDailyDateChange,
                    onTransactionClick = onEditTransaction
                )
                3 -> AnalyticsScreen(
                    transactions = transactions
                )
                4 -> ProfileScreen(
                    userProfile = userProfile,
                    transactions = transactions,
                    totalIncome = summary.totalIncome,
                    totalExpense = summary.totalExpense,
                    balance = summary.balance,
                    onToggleDarkMode = onToggleDarkMode,
                    onUpdateProfile = onUpdateProfile,
                    onResetDemoData = onResetDemoData,
                    onLogout = onLogout
                )
            }
        }
    }
}
