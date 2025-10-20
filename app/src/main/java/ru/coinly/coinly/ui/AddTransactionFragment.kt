package ru.coinly.coinly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.coinly.coinly.R
import ru.coinly.coinly.data.DBHelper

class AddTransactionFragment : Fragment() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var etAmount: EditText
    private lateinit var rgType: RadioGroup
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var dbHelper: DBHelper

    // Пример списка категорий
    private val categories = listOf("Еда", "Транспорт", "Развлечения", "Одежда", "Другое")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_transaction, container, false)

        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etAmount = view.findViewById(R.id.etAmount)
        rgType = view.findViewById(R.id.rgType)
        btnSave = view.findViewById(R.id.btnSaveTransaction)
        btnCancel = view.findViewById(R.id.btnCancelTransaction)
        dbHelper = DBHelper(requireContext())

        // Настраиваем список категорий
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Сохранить
        btnSave.setOnClickListener { saveTransaction() }

        // Отмена
        btnCancel.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    private fun saveTransaction() {
        val category = spinnerCategory.selectedItem as String
        val amountText = etAmount.text.toString().trim()

        if (amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Введите сумму", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.replace(',', '.').toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Введите корректную сумму", Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (rgType.checkedRadioButtonId == R.id.rbExpense) "expense" else "income"
        val date = System.currentTimeMillis()

        val ok = dbHelper.addTransaction(category, amount, type, date)
        if (ok) {
            Toast.makeText(requireContext(), "Операция сохранена", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // возвращаемся назад
        } else {
            Toast.makeText(requireContext(), "Ошибка записи в базу", Toast.LENGTH_SHORT).show()
        }
    }
}
