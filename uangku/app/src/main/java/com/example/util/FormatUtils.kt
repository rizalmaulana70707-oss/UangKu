package com.example.util

import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object FormatUtils {
    private val indonesianLocale = Locale("id", "ID")

    fun formatRupiah(amount: Double, withPrefix: Boolean = true): String {
        val formatter = NumberFormat.getNumberInstance(indonesianLocale)
        val formatted = formatter.format(abs(amount).toLong())
        val prefix = if (withPrefix) "Rp " else ""
        return if (amount < 0) "-$prefix$formatted" else "$prefix$formatted"
    }

    fun formatCompactRupiah(amount: Double): String {
        val absAmount = abs(amount)
        return when {
            absAmount >= 1_000_000_000 -> String.format(indonesianLocale, "%.1f M", absAmount / 1_000_000_000)
            absAmount >= 1_000_000 -> String.format(indonesianLocale, "%.1f jt", absAmount / 1_000_000)
            absAmount >= 1_000 -> String.format(indonesianLocale, "%.0f rb", absAmount / 1_000)
            else -> absAmount.toLong().toString()
        }
    }

    fun formatDate(timestamp: Long, pattern: String = "dd MMMM yyyy"): String {
        val sdf = SimpleDateFormat(pattern, indonesianLocale)
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", indonesianLocale)
        return sdf.format(Date(timestamp))
    }

    fun formatRelativeDate(timestamp: Long): String {
        val nowCal = Calendar.getInstance()
        val transCal = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isSameYear = nowCal.get(Calendar.YEAR) == transCal.get(Calendar.YEAR)
        val isSameDay = isSameYear && nowCal.get(Calendar.DAY_OF_YEAR) == transCal.get(Calendar.DAY_OF_YEAR)

        nowCal.add(Calendar.DAY_OF_YEAR, -1)
        val isYesterday = isSameYear && nowCal.get(Calendar.DAY_OF_YEAR) == transCal.get(Calendar.DAY_OF_YEAR)

        return when {
            isSameDay -> "Hari ini"
            isYesterday -> "Kemarin"
            isSameYear -> formatDate(timestamp, "EEEE, dd MMM")
            else -> formatDate(timestamp, "dd MMM yyyy")
        }
    }

    fun formatMonthYear(cal: Calendar): String {
        val sdf = SimpleDateFormat("MMMM yyyy", indonesianLocale)
        return sdf.format(cal.time)
    }

    fun exportToCsv(transactions: List<TransactionItem>): String {
        val sb = StringBuilder()
        sb.append("ID,Tanggal,Waktu,Tipe,Kategori,Judul,Nominal,Catatan\n")
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.US)
        for (t in transactions) {
            val dateStr = dateFmt.format(Date(t.timestamp))
            val timeStr = timeFmt.format(Date(t.timestamp))
            val titleEscaped = "\"${t.title.replace("\"", "\"\"")}\""
            val noteEscaped = "\"${t.note.replace("\"", "\"\"")}\""
            sb.append("${t.id},$dateStr,$timeStr,${t.type.name},\"${t.categoryName}\",$titleEscaped,${t.amount.toLong()},$noteEscaped\n")
        }
        return sb.toString()
    }

    fun exportToSummaryReport(
        transactions: List<TransactionItem>,
        totalIncome: Double,
        totalExpense: Double,
        balance: Double,
        userName: String
    ): String {
        val sb = StringBuilder()
        sb.append("=========================================\n")
        sb.append("         LAPORAN KEUANGAN UANGKU         \n")
        sb.append("=========================================\n")
        sb.append("Nama Pengguna : $userName\n")
        sb.append("Tanggal Cetak : ${formatDate(System.currentTimeMillis(), "dd MMMM yyyy HH:mm")}\n")
        sb.append("-----------------------------------------\n")
        sb.append("RINGKASAN:\n")
        sb.append("Total Pemasukan   : ${formatRupiah(totalIncome)}\n")
        sb.append("Total Pengeluaran : ${formatRupiah(totalExpense)}\n")
        sb.append("Saldo Bersih      : ${formatRupiah(balance)}\n")
        sb.append("Total Transaksi   : ${transactions.size}\n")
        sb.append("-----------------------------------------\n")
        sb.append("DAFTAR TRANSAKSI TERAKHIR:\n")
        for ((idx, t) in transactions.take(20).withIndex()) {
            val sign = if (t.type == TransactionType.INCOME) "(+)" else "(-)"
            sb.append("${idx + 1}. [${formatDate(t.timestamp, "dd/MM/yy")}] ${t.title} - ${t.categoryName}\n")
            sb.append("   $sign ${formatRupiah(t.amount)}\n")
            if (t.note.isNotBlank()) {
                sb.append("   Catatan: ${t.note}\n")
            }
        }
        sb.append("=========================================\n")
        sb.append("Dibuat otomatis oleh Aplikasi UangKu\n")
        return sb.toString()
    }
}
