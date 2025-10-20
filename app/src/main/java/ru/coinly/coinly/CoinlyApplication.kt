package ru.coinly.coinly

import android.app.Application
import ru.coinly.coinly.data.database.AppDatabase

class CoinlyApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
    }
}
