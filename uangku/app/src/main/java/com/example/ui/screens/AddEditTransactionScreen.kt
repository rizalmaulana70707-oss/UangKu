package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.FormatUtils
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AddEditTransactionScreen(
    initialType: TransactionType = TransactionType.EXPENSE,
    existingTransaction: TransactionItem? = null,
    onBackClick: () -> Unit,
    onSaveTransaction: (
        id: Long?,
        title: String,
        amount: Double,
        type: TransactionType,
        category: Category,
        timestamp: Long,
        note: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedType by remember {
        mutableStateOf(existingTransaction?.type ?: initialType)
    }

    var title by remember {
        mutableStateOf(existingTransaction?.title ?: "")
    }

    var amountText by remember {
        mutableStateOf(
            if (existingTransaction != null) {
                existingTransaction.amount.toLong().toString()
            } else ""
        )
    }

    var note by remember {
        mutableStateOf(existingTransaction?.note ?: "")
    }

    var selectedTimestamp by remember {
        mutableLongStateOf(existingTransaction?.timestamp ?: System.currentTimeMillis())
    }

    val availableCategories = remember(selectedType) {
        Category.getCategoriesForType(selectedType)
    }

    var selectedCategory by remember(selectedType) {
        mutableStateOf(
            if (existingTransaction != null && existingTransaction.type == selectedType) {
                Category.findCategory(existingTransaction.categoryId, selectedType)
            } else {
                availableCategories.first()
            }
        )
    }

    // Quick add chips
    val quickAmounts = listOf(20_000L, 50_000L, 100_000L, 250_000L, 500_000L, 1_000_000L)

    // Date Picker Dialog setup
    val cal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                selectedTimestamp = newCal.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Top Bar: Back button + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("add_transaction_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = if (existingTransaction == null) "Tambah Transaksi" else "Edit Transaksi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Transaction Type Selector (Pemasukan vs Pengeluaran)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Pengeluaran Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedType == TransactionType.EXPENSE) ExpenseRed else Color.Transparent
                        )
                        .clickable {
                            selectedType = TransactionType.EXPENSE
                        }
                        .padding(vertical = 12.dp)
                        .testTag("select_expense_type"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pengeluaran",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Pemasukan Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedType == TransactionType.INCOME) IncomeGreen else Color.Transparent
                        )
                        .clickable {
                            selectedType = TransactionType.INCOME
                        }
                        .padding(vertical = 12.dp)
                        .testTag("select_income_type"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pemasukan",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.INCOME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Input Nominal
            Text(
                text = "Nominal Transaksi",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        amountText = input
                    }
                },
                prefix = {
                    Text(
                        text = "Rp ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed
                        )
                    )
                },
                placeholder = { Text("0") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_amount"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick nominal chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickAmounts.take(4).forEach { amt ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val current = amountText.toLongOrNull() ?: 0L
                                amountText = (current + amt).toString()
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "+${FormatUtils.formatCompactRupiah(amt.toDouble())}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Judul / Nama Transaksi
            Text(
                text = "Nama / Judul Transaksi",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(if (selectedType == TransactionType.INCOME) "Contoh: Gaji Bulanan, Bonus Freelance" else "Contoh: Makan Siang, Bensin, Belanja")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_title"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pilihan Tanggal
            Text(
                text = "Tanggal Transaksi",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { datePickerDialog.show() }
                    .testTag("pick_date_button"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FormatUtils.formatDate(selectedTimestamp, "EEEE, dd MMMM yyyy"),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Pilih Tanggal",
                        tint = BluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pilihan Kategori
            Text(
                text = "Pilih Kategori",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories Grid / Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunked = availableCategories.chunked(2)
                chunked.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { cat ->
                            val isSelected = selectedCategory.id == cat.id
                            val catColor = Color(cat.colorValue)

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedCategory = cat }
                                    .testTag("category_chip_${cat.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) catColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, catColor) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(catColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = CategoryIconHelper.getIcon(cat.iconKey),
                                            contentDescription = null,
                                            tint = catColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cat.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Catatan (Opsional)
            Text(
                text = "Catatan (Opsional)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Tambahkan rincian atau keterangan...") },
                singleLine = false,
                maxLines = 3,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_note"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Tombol "Simpan"
            Button(
                onClick = {
                    val amountValue = amountText.toDoubleOrNull() ?: 0.0
                    if (amountValue <= 0) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Silakan masukkan nominal transaksi yang valid")
                        }
                        return@Button
                    }
                    val finalTitle = if (title.isBlank()) selectedCategory.name else title
                    onSaveTransaction(
                        existingTransaction?.id,
                        finalTitle,
                        amountValue,
                        selectedType,
                        selectedCategory,
                        selectedTimestamp,
                        note
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.INCOME) IncomeGreen else BluePrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingTransaction == null) "Simpan Transaksi" else "Perbarui Transaksi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
