package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.ui.components.ReportSummaryCards
import com.example.ui.components.TransactionListItem
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    transactions: List<TransactionItem>,
    selectedDailyDate: Long,
    onDailyDateChange: (Long) -> Unit,
    onTransactionClick: (TransactionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Harian, 1 = Mingguan, 2 = Bulanan
    val tabTitles = listOf("Harian", "Mingguan", "Bulanan")

    // State for weekly & monthly navigation
    var weekOffset by remember { mutableIntStateOf(0) }
    var monthOffset by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Laporan Keuangan",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 3-tab switcher (Harian, Mingguan, Bulanan)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = BluePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BluePrimary,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selectedTab == index) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("report_tab_$index")
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        when (selectedTab) {
            0 -> DailyReportContent(
                transactions = transactions,
                selectedDate = selectedDailyDate,
                onDateChange = onDailyDateChange,
                onTransactionClick = onTransactionClick
            )
            1 -> WeeklyReportContent(
                transactions = transactions,
                weekOffset = weekOffset,
                onWeekOffsetChange = { weekOffset = it },
                onTransactionClick = onTransactionClick
            )
            2 -> MonthlyReportContent(
                transactions = transactions,
                monthOffset = monthOffset,
                onMonthOffsetChange = { monthOffset = it },
                onTransactionClick = onTransactionClick
            )
        }
    }
}

// 1. Daily Report Content
@Composable
private fun DailyReportContent(
    transactions: List<TransactionItem>,
    selectedDate: Long,
    onDateChange: (Long) -> Unit,
    onTransactionClick: (TransactionItem) -> Unit
) {
    val context = LocalContext.current
    val cal = remember(selectedDate) { Calendar.getInstance().apply { timeInMillis = selectedDate } }

    val datePicker = remember(selectedDate) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
                onDateChange(newCal.timeInMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Filter transactions for that specific day
    val dayTransactions = remember(transactions, selectedDate) {
        val startCal = Calendar.getInstance().apply {
            timeInMillis = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            timeInMillis = selectedDate
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        transactions.filter { it.timestamp in startCal.timeInMillis..endCal.timeInMillis }
    }

    val income = dayTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expense = dayTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val balance = income - expense

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Selector Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val prev = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            add(Calendar.DAY_OF_YEAR, -1)
                        }
                        onDateChange(prev.timeInMillis)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Hari Sebelumnya")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { datePicker.show() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = FormatUtils.formatDate(selectedDate, "dd MMMM yyyy"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {
                        val next = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                        onDateChange(next.timeInMillis)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Hari Selanjutnya")
                    }
                }
            }
        }

        // Summary Cards (Pemasukan, Pengeluaran, Selisih)
        item {
            ReportSummaryCards(income = income, expense = expense, balance = balance)
        }

        // Transactions list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rincian Transaksi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${dayTransactions.size} Transaksi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (dayTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada transaksi pada tanggal ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(dayTransactions, key = { it.id }) { item ->
                TransactionListItem(
                    transaction = item,
                    onClick = { onTransactionClick(item) }
                )
            }
        }
    }
}

// 2. Weekly Report Content
@Composable
private fun WeeklyReportContent(
    transactions: List<TransactionItem>,
    weekOffset: Int,
    onWeekOffsetChange: (Int) -> Unit,
    onTransactionClick: (TransactionItem) -> Unit
) {
    val weekCal = remember(weekOffset) {
        Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            add(Calendar.WEEK_OF_YEAR, weekOffset)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val startOfWeek = weekCal.timeInMillis
    val endOfWeek = Calendar.getInstance().apply {
        timeInMillis = startOfWeek
        add(Calendar.DAY_OF_YEAR, 6)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val weekTransactions = remember(transactions, weekOffset) {
        transactions.filter { it.timestamp in startOfWeek..endOfWeek }
    }

    val totalIncome = weekTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = weekTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    // Daily breakdown for each of the 7 days
    val dayNames = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val dailyBreakdowns = remember(weekTransactions, weekOffset) {
        (0..6).map { dayIdx ->
            val dayStart = Calendar.getInstance().apply {
                timeInMillis = startOfWeek
                add(Calendar.DAY_OF_YEAR, dayIdx)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            val dayEnd = Calendar.getInstance().apply {
                timeInMillis = dayStart
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            val itemsForDay = weekTransactions.filter { it.timestamp in dayStart..dayEnd }
            val inc = itemsForDay.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val exp = itemsForDay.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            Triple(dayNames[dayIdx], dayStart, Pair(inc, exp))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Week Navigator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onWeekOffsetChange(weekOffset - 1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Minggu Sebelumnya")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (weekOffset == 0) "Minggu Ini" else if (weekOffset == -1) "Minggu Lalu" else "Periode Minggu",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${FormatUtils.formatDate(startOfWeek, "dd MMM")} - ${FormatUtils.formatDate(endOfWeek, "dd MMM yyyy")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { onWeekOffsetChange(weekOffset + 1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Minggu Selanjutnya")
                    }
                }
            }
        }

        // Summary Cards
        item {
            ReportSummaryCards(income = totalIncome, expense = totalExpense, balance = netBalance)
        }

        // Daily Breakdown Cards
        item {
            Text(
                text = "Ringkasan Pemasukan & Pengeluaran Per Hari",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    dailyBreakdowns.forEach { (dayName, dateMillis, amounts) ->
                        val (inc, exp) = amounts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = FormatUtils.formatDate(dateMillis, "dd MMM"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "+${FormatUtils.formatCompactRupiah(inc)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (inc > 0) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "-${FormatUtils.formatCompactRupiah(exp)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (exp > 0) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    }
                }
            }
        }

        // List of transactions during this week
        item {
            Text(
                text = "Rincian Transaksi Minggu Ini",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (weekTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada transaksi di minggu ini", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(weekTransactions, key = { it.id }) { item ->
                TransactionListItem(
                    transaction = item,
                    onClick = { onTransactionClick(item) }
                )
            }
        }
    }
}

// 3. Monthly Report Content
@Composable
private fun MonthlyReportContent(
    transactions: List<TransactionItem>,
    monthOffset: Int,
    onMonthOffsetChange: (Int) -> Unit,
    onTransactionClick: (TransactionItem) -> Unit
) {
    val monthCal = remember(monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val startOfMonth = monthCal.timeInMillis
    val endOfMonth = Calendar.getInstance().apply {
        timeInMillis = startOfMonth
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val monthTransactions = remember(transactions, monthOffset) {
        transactions.filter { it.timestamp in startOfMonth..endOfMonth }
    }

    val totalIncome = monthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = monthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    // Weekly breakdown inside month (Weeks 1 to 4/5)
    val weeklyBreakdowns = remember(monthTransactions, monthOffset) {
        val totalDays = Calendar.getInstance().apply { timeInMillis = startOfMonth }.getActualMaximum(Calendar.DAY_OF_MONTH)
        val weeks = mutableListOf<Triple<String, Double, Double>>()
        var currentDay = 1
        var weekNumber = 1

        while (currentDay <= totalDays) {
            val weekEndDay = (currentDay + 6).coerceAtMost(totalDays)
            val wStart = Calendar.getInstance().apply {
                timeInMillis = startOfMonth
                set(Calendar.DAY_OF_MONTH, currentDay)
                set(Calendar.HOUR_OF_DAY, 0)
            }.timeInMillis
            val wEnd = Calendar.getInstance().apply {
                timeInMillis = startOfMonth
                set(Calendar.DAY_OF_MONTH, weekEndDay)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
            }.timeInMillis

            val itemsInWeek = monthTransactions.filter { it.timestamp in wStart..wEnd }
            val inc = itemsInWeek.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val exp = itemsInWeek.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            weeks.add(Triple("Minggu $weekNumber ($currentDay - $weekEndDay ${FormatUtils.formatDate(startOfMonth, "MMM")})", inc, exp))
            currentDay = weekEndDay + 1
            weekNumber++
        }
        weeks
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Selector Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onMonthOffsetChange(monthOffset - 1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Bulan Sebelumnya")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = FormatUtils.formatDate(startOfMonth, "MMMM yyyy"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { onMonthOffsetChange(monthOffset + 1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Bulan Selanjutnya")
                    }
                }
            }
        }

        // Summary Cards
        item {
            ReportSummaryCards(income = totalIncome, expense = totalExpense, balance = netBalance)
        }

        // Weekly Breakdown inside Month
        item {
            Text(
                text = "Rincian Berdasarkan Minggu",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    weeklyBreakdowns.forEach { (weekLabel, inc, exp) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = weekLabel,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "+${FormatUtils.formatCompactRupiah(inc)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (inc > 0) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "-${FormatUtils.formatCompactRupiah(exp)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (exp > 0) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    }
                }
            }
        }

        // List of transactions in this month
        item {
            Text(
                text = "Semua Transaksi Bulan Ini (${monthTransactions.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (monthTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada transaksi di bulan ini", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(monthTransactions, key = { it.id }) { item ->
                TransactionListItem(
                    transaction = item,
                    onClick = { onTransactionClick(item) }
                )
            }
        }
    }
}
