package ru.coinly.coinly.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.coinly.coinly.data.entity.Transaction

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<Transaction>

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
