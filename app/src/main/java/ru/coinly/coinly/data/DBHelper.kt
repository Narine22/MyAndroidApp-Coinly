package ru.coinly.coinly.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context) : SQLiteOpenHelper(context, "coinly.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        try {
            // --- Таблица пользователей ---
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE,
                    email TEXT,
                    password TEXT
                )
                """.trimIndent()
            )

            // --- Таблица транзакций ---
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category TEXT,
                    amount REAL,
                    date INTEGER,
                    type TEXT
                )
                """.trimIndent()
            )

            Log.d("DBHelper", "Database tables created successfully.")
        } catch (e: Exception) {
            Log.e("DBHelper", "Error creating tables: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category TEXT,
                    amount REAL,
                    date INTEGER,
                    type TEXT
                )
                """.trimIndent()
            )
        }
    }

    // ====================== USERS =========================

    fun addUser(username: String, email: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", password.hashCode().toString())
        }
        val result = db.insert("users", null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE username = ? AND password = ?",
            arrayOf(username, password.hashCode().toString())
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    // ====================== TRANSACTIONS =========================

    fun addTransaction(category: String, amount: Double, type: String, date: Long): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("category", category)
            put("amount", amount)
            put("date", date)
            put("type", type)
        }
        val result = db.insert("transactions", null, values)
        db.close()
        return result != -1L
    }

    fun getAllTransactions(): List<TransactionData> {
        val transactions = mutableListOf<TransactionData>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transactions", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                transactions.add(TransactionData(id, category, amount, date, type))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return transactions
    }

    fun clearDatabase() {
        val db = writableDatabase
        db.execSQL("DELETE FROM transactions")
        db.close()
    }
    // Возвращает сумму расходов (type = "expense") по переданной категории
    fun getSumByCategory(category: String): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(amount) as s FROM transactions WHERE category = ? AND type = ?",
            arrayOf(category, "expense")
        )
        var sum = 0.0
        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(cursor.getColumnIndexOrThrow("s"))
        }
        cursor.close()
        db.close()
        return sum
    }

    // Возвращает общую сумму расходов (type = "expense")
    fun getTotalExpenses(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(amount) as s FROM transactions WHERE type = ?",
            arrayOf("expense")
        )
        var sum = 0.0
        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(cursor.getColumnIndexOrThrow("s"))
        }
        cursor.close()
        db.close()
        return sum
    }

    // (опционально) Получить map всех категорий, которые есть в таблице (distinct)
// НО мы будем использовать мастер-список категорий для показа всегда
    fun getDistinctCategories(): List<String> {
        val list = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT category FROM transactions", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndexOrThrow("category")))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

}

// --- Модель транзакции ---
data class TransactionData(
    val id: Int = 0,
    val category: String,
    val amount: Double,
    val date: Long,
    val type: String
)
