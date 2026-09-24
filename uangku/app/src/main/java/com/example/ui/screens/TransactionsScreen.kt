package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.TransactionListItem
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.FormatUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactions: List<TransactionItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedTypeFilter: TransactionType?,
    onTypeFilterChange: (TransactionType?) -> Unit,
    onEditTransaction: (TransactionItem) -> Unit,
    onDeleteTransaction: (TransactionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var detailTransaction by remember { mutableStateOf<TransactionItem?>(null) }
    var transactionToDelete by remember { mutableStateOf<TransactionItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Group transactions by date
    val groupedTransactions = remember(transactions) {
        transactions.groupBy { item ->
            FormatUtils.formatRelativeDate(item.timestamp)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Riwayat Transaksi",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Surface(
                color = BluePrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${transactions.size} Data",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Cari transaksi, kategori, catatan...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Hapus pencarian",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("search_transaction_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips: Semua, Pemasukan, Pengeluaran
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTypeFilter == null,
                onClick = { onTypeFilterChange(null) },
                label = { Text("Semua") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BluePrimary,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_all")
            )

            FilterChip(
                selected = selectedTypeFilter == TransactionType.INCOME,
                onClick = { onTypeFilterChange(TransactionType.INCOME) },
                label = { Text("Pemasukan") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = IncomeGreen,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_income")
            )

            FilterChip(
                selected = selectedTypeFilter == TransactionType.EXPENSE,
                onClick = { onTypeFilterChange(TransactionType.EXPENSE) },
                label = { Text("Pengeluaran") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ExpenseRed,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_expense")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List Grouped by Date
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "Tidak ditemukan transaksi sesuai kata kunci" else "Belum ada riwayat transaksi",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedTransactions.forEach { (dateGroup, itemsInGroup) ->
                    item {
                        Text(
                            text = dateGroup,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }

                    items(itemsInGroup, key = { it.id }) { transaction ->
                        TransactionListItem(
                            transaction = transaction,
                            onClick = { detailTransaction = transaction }
                        )
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    detailTransaction?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { detailTransaction = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val isIncome = item.type == TransactionType.INCOME
            val catColor = Color(item.categoryColor)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(catColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIconHelper.getIcon(item.categoryIcon),
                        contentDescription = null,
                        tint = catColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${if (isIncome) "+" else "-"} ${FormatUtils.formatRupiah(item.amount)}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isIncome) IncomeGreen else ExpenseRed
                    ),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.categoryName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                // Detail info rows
                DetailRow(label = "Tipe", value = if (isIncome) "Pemasukan" else "Pengeluaran")
                DetailRow(label = "Tanggal", value = FormatUtils.formatDate(item.timestamp, "EEEE, dd MMMM yyyy"))
                DetailRow(label = "Waktu", value = "${FormatUtils.formatTime(item.timestamp)} WIB")
                if (item.note.isNotBlank()) {
                    DetailRow(label = "Catatan", value = item.note)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons: Edit & Hapus
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val toEdit = item
                            detailTransaction = null
                            onEditTransaction(toEdit)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("edit_transaction_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit")
                    }

                    Button(
                        onClick = {
                            val toDel = item
                            detailTransaction = null
                            transactionToDelete = toDel
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("delete_transaction_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hapus")
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Delete
    transactionToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Hapus Transaksi?") },
            text = { Text("Apakah Anda yakin ingin menghapus transaksi \"${item.title}\"? Data yang dihapus tidak dapat dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTransaction(item)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
