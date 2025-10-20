package ru.coinly.coinly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.coinly.coinly.data.DBHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Настройка навигации ---
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // --- Работа с базой данных ---
        val db = DBHelper(this)

        // Добавляем тестовые транзакции, если база пуста
        if (db.getAllTransactions().isEmpty()) {
            db.addTransaction("Еда", 10000.0, "expense", System.currentTimeMillis())
            db.addTransaction("Транспорт", 6250.0, "expense", System.currentTimeMillis())
            db.addTransaction("Развлечения", 8750.0, "expense", System.currentTimeMillis())
        }
    }
}
