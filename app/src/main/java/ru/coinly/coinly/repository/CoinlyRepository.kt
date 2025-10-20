package ru.coinly.coinly.repository

import android.content.Context
import ru.coinly.coinly.data.DBHelper
import ru.coinly.coinly.data.database.AppDatabase
import ru.coinly.coinly.data.entity.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoinlyRepository(private val context: Context) {

    private val dbHelper = DBHelper(context)
    private val appDatabase = AppDatabase.getDatabase(context)
    private val transactionDao = appDatabase.transactionDao()

    // Пример добавления транзакции
    suspend fun insertTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            transactionDao.insert(transaction)
        }
    }

    // Получение всех транзакций
    suspend fun getAllTransactions(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            transactionDao.getAll()
        }
    }

    // Очистка базы данных (пример)
    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            dbHelper.clearDatabase()
        }
    }
}
