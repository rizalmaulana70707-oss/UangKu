package com.example.data.model

data class Category(
    val id: String,
    val name: String,
    val type: TransactionType,
    val iconKey: String,
    val colorValue: Long
) {
    companion object {
        val DefaultExpenseCategories = listOf(
            Category("food", "Makan & Minum", TransactionType.EXPENSE, "restaurant", 0xFFF97316),
            Category("transport", "Transportasi", TransactionType.EXPENSE, "commute", 0xFF06B6D4),
            Category("shopping", "Belanja", TransactionType.EXPENSE, "shopping_bag", 0xFF8B5CF6),
            Category("bills", "Tagihan", TransactionType.EXPENSE, "receipt_long", 0xFFEC4899),
            Category("entertainment", "Hiburan", TransactionType.EXPENSE, "movie", 0xFF3B82F6),
            Category("health", "Kesehatan", TransactionType.EXPENSE, "medical_services", 0xFF10B981),
            Category("education", "Pendidikan", TransactionType.EXPENSE, "school", 0xFFEAB308),
            Category("other_exp", "Lainnya", TransactionType.EXPENSE, "more_horiz", 0xFF64748B)
        )

        val DefaultIncomeCategories = listOf(
            Category("salary", "Gaji Pokok", TransactionType.INCOME, "account_balance_wallet", 0xFF059669),
            Category("bonus", "Bonus & THR", TransactionType.INCOME, "card_giftcard", 0xFF10B981),
            Category("investment", "Investasi", TransactionType.INCOME, "trending_up", 0xFF6366F1),
            Category("freelance", "Freelance", TransactionType.INCOME, "laptop_mac", 0xFF3B82F6),
            Category("other_inc", "Lainnya", TransactionType.INCOME, "monetization_on", 0xFF14B8A6)
        )

        fun getAllDefault(): List<Category> = DefaultExpenseCategories + DefaultIncomeCategories

        fun getCategoriesForType(type: TransactionType): List<Category> {
            return if (type == TransactionType.INCOME) DefaultIncomeCategories else DefaultExpenseCategories
        }

        fun findCategory(id: String, defaultType: TransactionType = TransactionType.EXPENSE): Category {
            return getAllDefault().firstOrNull { it.id == id } 
                ?: getCategoriesForType(defaultType).first()
        }
    }
}
