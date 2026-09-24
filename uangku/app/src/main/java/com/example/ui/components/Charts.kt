package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.FormatUtils
import kotlin.math.max

data class ChartPoint(
    val label: String,
    val income: Double,
    val expense: Double,
    val balance: Double = 0.0
)

data class CategorySlice(
    val categoryName: String,
    val amount: Double,
    val color: Color,
    val percentage: Float
)

// 1. Dual Bar Chart (Dashboard Overview)
@Composable
fun MonthlyBarOverviewChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ringkasan Bulanan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Legend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Masuk", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Keluar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (points.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                    Text("Belum ada data grafik", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val maxVal = points.maxOfOrNull { max(it.income, it.expense) }?.coerceAtLeast(1.0) ?: 1.0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = 14.dp.toPx()
                        val groupSpacing = size.width / points.size
                        val bottomY = size.height - 24.dp.toPx()
                        val chartHeight = bottomY - 10.dp.toPx()

                        // Grid baseline
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            start = Offset(0f, bottomY),
                            end = Offset(size.width, bottomY),
                            strokeWidth = 1.dp.toPx()
                        )

                        points.forEachIndexed { index, point ->
                            val centerX = (index * groupSpacing) + (groupSpacing / 2)

                            // Income bar
                            val incHeight = ((point.income / maxVal) * chartHeight).toFloat().coerceAtLeast(4f)
                            drawRoundRect(
                                color = IncomeGreen,
                                topLeft = Offset(centerX - barWidth - 2.dp.toPx(), bottomY - incHeight),
                                size = Size(barWidth, incHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )

                            // Expense bar
                            val expHeight = ((point.expense / maxVal) * chartHeight).toFloat().coerceAtLeast(4f)
                            drawRoundRect(
                                color = ExpenseRed,
                                topLeft = Offset(centerX + 2.dp.toPx(), bottomY - expHeight),
                                size = Size(barWidth, expHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )
                        }
                    }

                    // Labels beneath bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        points.forEach { pt ->
                            Text(
                                text = pt.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// 2. Line Chart: Income & Expense Curve
@Composable
fun IncomeExpenseLineChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kurva Pemasukan & Pengeluaran",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(IncomeGreen))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Masuk", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ExpenseRed))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Keluar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (points.size < 2) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    Text("Perlu minimal 2 data periode untuk grafik garis", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val maxVal = points.maxOfOrNull { max(it.income, it.expense) }?.coerceAtLeast(1.0) ?: 1.0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val bottomY = size.height - 24.dp.toPx()
                        val topY = 12.dp.toPx()
                        val chartHeight = bottomY - topY
                        val stepX = size.width / (points.size - 1).coerceAtLeast(1)

                        // 3 horizontal guideline dashes
                        for (i in 0..2) {
                            val lineY = topY + (chartHeight / 2) * i
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                start = Offset(0f, lineY),
                                end = Offset(size.width, lineY),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val incomePath = Path()
                        val expensePath = Path()

                        val incomePoints = mutableListOf<Offset>()
                        val expensePoints = mutableListOf<Offset>()

                        points.forEachIndexed { i, pt ->
                            val x = i * stepX
                            val incY = bottomY - ((pt.income / maxVal) * chartHeight).toFloat()
                            val expY = bottomY - ((pt.expense / maxVal) * chartHeight).toFloat()

                            val incOffset = Offset(x, incY)
                            val expOffset = Offset(x, expY)

                            incomePoints.add(incOffset)
                            expensePoints.add(expOffset)

                            if (i == 0) {
                                incomePath.moveTo(x, incY)
                                expensePath.moveTo(x, expY)
                            } else {
                                incomePath.lineTo(x, incY)
                                expensePath.lineTo(x, expY)
                            }
                        }

                        // Draw lines
                        drawPath(incomePath, color = IncomeGreen, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                        drawPath(expensePath, color = ExpenseRed, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

                        // Draw Point dots
                        incomePoints.forEach { pt ->
                            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                            drawCircle(color = IncomeGreen, radius = 3.5.dp.toPx(), center = pt)
                        }
                        expensePoints.forEach { pt ->
                            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                            drawCircle(color = ExpenseRed, radius = 3.5.dp.toPx(), center = pt)
                        }
                    }

                    // Bottom labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        points.forEach { pt ->
                            Text(
                                text = pt.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// 3. Donut / Pie Chart (Category Expense)
@Composable
fun CategoryDonutChart(
    slices: List<CategorySlice>,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Pengeluaran Berdasarkan Kategori",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (slices.isEmpty() || totalExpense <= 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada pengeluaran pada periode ini",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Donut with center total
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(180.dp)) {
                        var startAngle = -90f
                        val strokeWidth = 28.dp.toPx()

                        slices.forEach { slice ->
                            val sweep = (slice.percentage / 100f) * 360f
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = sweep.coerceAtLeast(1f),
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += sweep
                        }
                    }

                    // Center text showing total expense
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Keluar",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = FormatUtils.formatCompactRupiah(totalExpense),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown list of categories with percentage
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    slices.forEach { slice ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(slice.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = slice.categoryName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = FormatUtils.formatRupiah(slice.amount),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = slice.color.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = String.format("%.1f%%", slice.percentage),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = slice.color,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. Line Chart: Balance Trend Chart
@Composable
fun BalanceTrendChart(
    points: List<ChartPoint>,
    endingBalance: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tren Perkembangan Saldo",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Saldo Akhir: ${FormatUtils.formatRupiah(endingBalance)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (endingBalance >= 0) BluePrimary else ExpenseRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Surface(
                    color = BluePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Akumulasi",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (points.size < 2) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    Text("Perlu minimal 2 data periode untuk melihat tren saldo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val minVal = points.minOfOrNull { it.balance }?.coerceAtMost(0.0) ?: 0.0
                val maxVal = points.maxOfOrNull { it.balance }?.coerceAtLeast(1.0) ?: 1.0
                val range = (maxVal - minVal).coerceAtLeast(1.0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val bottomY = size.height - 24.dp.toPx()
                        val topY = 12.dp.toPx()
                        val chartHeight = bottomY - topY
                        val stepX = size.width / (points.size - 1).coerceAtLeast(1)

                        val path = Path()
                        val fillPath = Path()
                        val chartPoints = mutableListOf<Offset>()

                        points.forEachIndexed { i, pt ->
                            val x = i * stepX
                            val normalized = ((pt.balance - minVal) / range).toFloat()
                            val y = bottomY - (normalized * chartHeight)
                            val offset = Offset(x, y)
                            chartPoints.add(offset)

                            if (i == 0) {
                                path.moveTo(x, y)
                                fillPath.moveTo(x, bottomY)
                                fillPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }
                        }

                        // Close fill path
                        fillPath.lineTo(chartPoints.last().x, bottomY)
                        fillPath.close()

                        // Draw subtle gradient fill below line
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BlueSecondary.copy(alpha = 0.35f),
                                    BlueSecondary.copy(alpha = 0.02f)
                                )
                            )
                        )

                        // Draw trend line
                        drawPath(
                            path = path,
                            color = BluePrimary,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw points
                        chartPoints.forEach { pt ->
                            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                            drawCircle(color = BluePrimary, radius = 3.5.dp.toPx(), center = pt)
                        }
                    }

                    // Date labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        points.forEach { pt ->
                            Text(
                                text = pt.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
