package ru.coinly.coinly.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.coinly.coinly.data.entity.Expense

data class CategorySum(
    val category: String,
    val total: Double
)

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    suspend fun getSumByCategory(): List<CategorySum>
}
