package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.TransactionItem
import com.example.data.model.UserProfile
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.FormatUtils

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    transactions: List<TransactionItem>,
    totalIncome: Double,
    totalExpense: Double,
    balance: Double,
    onToggleDarkMode: () -> Unit,
    onUpdateProfile: (name: String, email: String, monthlyBudget: Double, reminder: Boolean, reminderTime: String) -> Unit,
    onResetDemoData: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showCategoriesDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profil Pengguna",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = { showAboutDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("profile_settings_icon")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Avatar Card & User Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userProfile.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = userProfile.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = BlueContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Akun Aktif",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Pengaturan Keuangan
        item {
            Text(
                text = "Pengaturan Keuangan",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.AccountBalance,
                        title = "Batas Anggaran Bulanan",
                        subtitle = "Target: ${FormatUtils.formatRupiah(userProfile.monthlyBudget)}",
                        onClick = { showBudgetDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ProfileMenuItem(
                        icon = Icons.Default.Category,
                        title = "Kategori Transaksi",
                        subtitle = "Lihat daftar kategori pemasukan & pengeluaran",
                        onClick = { showCategoriesDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    // Reminder toggle row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Pengingat Keuangan",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (userProfile.isReminderEnabled) "Aktif setiap ${userProfile.reminderTime} WIB" else "Nonaktif",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = userProfile.isReminderEnabled,
                            onCheckedChange = { isEnabled ->
                                onUpdateProfile(
                                    userProfile.name,
                                    userProfile.email,
                                    userProfile.monthlyBudget,
                                    isEnabled,
                                    userProfile.reminderTime
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BluePrimary),
                            modifier = Modifier.testTag("toggle_reminder_switch")
                        )
                    }
                }
            }
        }

        // Section: Data & Tampilan
        item {
            Text(
                text = "Data & Tampilan",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Download,
                        title = "Export Data Transaksi",
                        subtitle = "Unduh format CSV atau laporan teks ringkas",
                        onClick = { showExportDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    // Dark Mode toggle row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Mode Gelap (Dark Mode)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (userProfile.isDarkMode) "Aktif" else "Nonaktif",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = userProfile.isDarkMode,
                            onCheckedChange = { onToggleDarkMode() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BluePrimary),
                            modifier = Modifier.testTag("toggle_dark_mode_switch")
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ProfileMenuItem(
                        icon = Icons.Default.Refresh,
                        title = "Muat Ulang Data Contoh",
                        subtitle = "Isi ulang data transaksi simulasi yang realistis",
                        onClick = {
                            onResetDemoData()
                            Toast.makeText(context, "Data contoh UangKu berhasil dimuat!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Section: Lainnya & Keluar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "Tentang Aplikasi",
                        subtitle = "UangKu v1.0.0 (Fintech Edition)",
                        onClick = { showAboutDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ProfileMenuItem(
                        icon = Icons.Default.Logout,
                        title = "Keluar Akun",
                        subtitle = "Keluar dari sesi saat ini",
                        iconTint = ExpenseRed,
                        onClick = { showLogoutDialog = true }
                    )
                }
            }
        }
    }

    // Dialog: Ubah Anggaran Bulanan
    if (showBudgetDialog) {
        var budgetInput by remember { mutableStateOf(userProfile.monthlyBudget.toLong().toString()) }
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("Batas Anggaran Bulanan") },
            text = {
                Column {
                    Text("Tentukan batas pengeluaran maksimal Anda setiap bulan:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) budgetInput = it },
                        prefix = { Text("Rp ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newBudget = budgetInput.toDoubleOrNull() ?: userProfile.monthlyBudget
                    onUpdateProfile(userProfile.name, userProfile.email, newBudget, userProfile.isReminderEnabled, userProfile.reminderTime)
                    showBudgetDialog = false
                    Toast.makeText(context, "Anggaran berhasil disimpan!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) { Text("Batal") }
            }
        )
    }

    // Dialog: Daftar Kategori Transaksi
    if (showCategoriesDialog) {
        AlertDialog(
            onDismissRequest = { showCategoriesDialog = false },
            title = { Text("Kategori Transaksi UangKu") },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text("Kategori Pengeluaran:", fontWeight = FontWeight.Bold, color = ExpenseRed)
                    }
                    items(Category.DefaultExpenseCategories) { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(cat.colorValue).copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                Icon(CategoryIconHelper.getIcon(cat.iconKey), contentDescription = null, tint = Color(cat.colorValue), modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Kategori Pemasukan:", fontWeight = FontWeight.Bold, color = IncomeGreen)
                    }
                    items(Category.DefaultIncomeCategories) { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(cat.colorValue).copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                Icon(CategoryIconHelper.getIcon(cat.iconKey), contentDescription = null, tint = Color(cat.colorValue), modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoriesDialog = false }) { Text("Tutup") }
            }
        )
    }

    // Dialog: Export Data
    if (showExportDialog) {
        val reportText = remember(transactions) {
            FormatUtils.exportToSummaryReport(transactions, totalIncome, totalExpense, balance, userProfile.name)
        }
        val csvText = remember(transactions) {
            FormatUtils.exportToCsv(transactions)
        }

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data Keuangan") },
            text = {
                Column {
                    Text("Pilih format untuk mengekspor data transaksi Anda:")
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Laporan UangKu", reportText))
                            Toast.makeText(context, "Laporan disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salin Ringkasan Laporan Teks")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("CSV Transaksi UangKu", csvText))
                            Toast.makeText(context, "Data CSV disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salin Data CSV (Excel)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) { Text("Selesai") }
            }
        )
    }

    // Dialog: Tentang Aplikasi
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Tentang UangKu") },
            text = {
                Column {
                    Text(
                        text = "UangKu - Solusi Manajemen Keuangan Pribadi Modern",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Versi: 1.0.0 (Fintech Edition)")
                    Text("Arsitektur: Kotlin, Jetpack Compose, Material 3, Room Local DB, MVVM")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dibuat untuk memudahkan Anda mencatat transaksi pemasukan, pengeluaran, memantau tren saldo, dan membuat keputusan finansial yang bijak.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Tutup") }
            }
        )
    }

    // Dialog: Logout confirmation
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar dari Akun?") },
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi UangKu?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = BluePrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
