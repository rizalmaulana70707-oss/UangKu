package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIcon(iconKey: String): ImageVector {
        return when (iconKey) {
            "restaurant" -> Icons.Default.Restaurant
            "commute" -> Icons.Default.Commute
            "shopping_bag" -> Icons.Default.ShoppingBag
            "receipt_long" -> Icons.Default.ReceiptLong
            "movie" -> Icons.Default.Movie
            "medical_services" -> Icons.Default.MedicalServices
            "school" -> Icons.Default.School
            "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
            "card_giftcard" -> Icons.Default.CardGiftcard
            "trending_up" -> Icons.Default.TrendingUp
            "laptop_mac" -> Icons.Default.LaptopMac
            "monetization_on" -> Icons.Default.LocalAtm
            "fastfood" -> Icons.Default.Fastfood
            "work" -> Icons.Default.Work
            else -> Icons.Default.MoreHoriz
        }
    }
}
