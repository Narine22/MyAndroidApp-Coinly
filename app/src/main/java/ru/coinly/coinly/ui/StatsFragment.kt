package ru.coinly.coinly.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import ru.coinly.coinly.R
import ru.coinly.coinly.data.DBHelper
import java.text.DecimalFormat

class StatsFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var pieChart: PieChart
    private lateinit var categoriesContainer: LinearLayout

    // Мастер-список категорий — всегда показываем их в интерфейсе
    private val masterCategories = listOf(
        "Еда" to "🍕",
        "Транспорт" to "🚗",
        "Развлечения" to "🎬",
        "Одежда" to "👕",
        "Здоровье" to "💊",
        "Прочее" to "📦"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        dbHelper = DBHelper(requireContext())

        pieChart = view.findViewById(R.id.pieChart)
        categoriesContainer = view.findViewById(R.id.categoriesContainer)

        // Обновляем при создании
        loadChartData()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Обновляем при возврате на экран (после добавления операции)
        loadChartData()
    }

    private fun loadChartData() {
        // 1) Получаем суммы из БД по каждой категории (только расходы)
        val categoryTotals = mutableMapOf<String, Double>()
        for ((name, _) in masterCategories) {
            // получаем сумму расходов для категории (DBHelper вернёт 0.0 если нет записей)
            categoryTotals[name] = dbHelper.getSumByCategory(name)
        }

        // 2) Общая сумма расходов (по всем категориям)
        val totalExpenses = categoryTotals.values.sum()

        // 3) Формируем PieChart — включаем в диаграмму только категории с суммой > 0
        val entries = categoryTotals
            .filter { it.value > 0.0 }
            .map { PieEntry(it.value.toFloat(), it.key) }

        val dataSet = PieDataSet(entries, "Расходы по категориям").apply {
            // Цвета соответствуют порядку masterCategories
            colors = listOf(
                Color.parseColor("#4CAF50"), // Еда
                Color.parseColor("#FF9800"), // Транспорт
                Color.parseColor("#E91E63"), // Развлечения
                Color.parseColor("#2196F3"), // Одежда
                Color.parseColor("#9C27B0"), // Здоровье
                Color.parseColor("#9E9E9E")  // Прочее
            )
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        pieChart.data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.centerText = "Расходы"
        pieChart.animateY(600)
        pieChart.invalidate()

        // 4) Обновляем список категорий под диаграммой — показываем все категории всегда
        categoriesContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val df = DecimalFormat("#,###")

        masterCategories.forEachIndexed { idx, (name, emoji) ->
            val amount = categoryTotals[name] ?: 0.0
            val percent = if (totalExpenses > 0.0) ((amount / totalExpenses) * 100).toInt() else 0

            val item = inflater.inflate(R.layout.item_category_stat, categoriesContainer, false)
            val textView = item.findViewById<TextView>(R.id.tvCategoryStat)
            val progressBar = item.findViewById<ProgressBar>(R.id.pbCategoryProgress)

            textView.text = "$emoji $name — ${df.format(amount)} ₽ (${percent}%)"
            progressBar.progress = percent

            val colors = listOf(
                "#4CAF50", "#FF9800", "#E91E63", "#2196F3", "#9C27B0", "#9E9E9E"
            )
            progressBar.progressTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor(colors[idx % colors.size]))

            categoriesContainer.addView(item)
        }
    }
}
