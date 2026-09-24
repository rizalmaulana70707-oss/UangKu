package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.FormatUtils

@Composable
fun BalanceHeroCard(
    balance: Double,
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            BluePrimaryDark,
                            BluePrimary,
                            BlueSecondary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // Top row: Label + visibility toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saldo Saat Ini",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }

                    IconButton(
                        onClick = { isBalanceVisible = !isBalanceVisible },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle saldo",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Balance Big Text
                Text(
                    text = if (isBalanceVisible) FormatUtils.formatRupiah(balance) else "••••••••",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Income & Expense summary badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Pemasukan
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(IncomeGreen.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Pemasukan",
                                tint = IncomeGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Pemasukan",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = if (isBalanceVisible) FormatUtils.formatRupiah(totalIncome) else "••••••",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Pengeluaran
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ExpenseRed.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Pengeluaran",
                                tint = ExpenseRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Pengeluaran",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = if (isBalanceVisible) FormatUtils.formatRupiah(totalExpense) else "••••••",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportSummaryCards(
    income: Double,
    expense: Double,
    balance: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummarySmallCard(
            title = "Pemasukan",
            amount = income,
            color = IncomeGreen,
            icon = Icons.Default.ArrowDownward,
            modifier = Modifier.weight(1f)
        )
        SummarySmallCard(
            title = "Pengeluaran",
            amount = expense,
            color = ExpenseRed,
            icon = Icons.Default.ArrowUpward,
            modifier = Modifier.weight(1f)
        )
        SummarySmallCard(
            title = "Selisih",
            amount = balance,
            color = if (balance >= 0) BlueSecondary else ExpenseRed,
            icon = Icons.Default.AccountBalanceWallet,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummarySmallCard(
    title: String,
    amount: Double,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = FormatUtils.formatRupiah(amount),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = color,
                maxLines = 1
            )
        }
    }
}
