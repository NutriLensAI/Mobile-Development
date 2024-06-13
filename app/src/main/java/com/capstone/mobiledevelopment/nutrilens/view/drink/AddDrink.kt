package com.capstone.mobiledevelopment.nutrilens.view.drink

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.mobiledevelopment.nutrilens.databinding.ActivityAddDrinkBinding
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.Drink
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.DrinkDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDrink : AppCompatActivity() {
    private lateinit var binding: ActivityAddDrinkBinding
    private val drinkDao by lazy { DrinkDatabase.getDatabase(this).drinkDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDrinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cupAmount = 200 // Assume each cup is 200 ml
        val sugarAmount = 25 // Assume each sugar amount is 25 grams

        binding.addCupButton.setOnClickListener {
            updateDrinkAmount(cupAmount)
        }

        binding.subtractCupButton.setOnClickListener {
            checkAndSubtractAmount(cupAmount)
        }

        binding.addSugarButton.setOnClickListener {
            updateSugarAmount(sugarAmount)
        }

        binding.subtractSugarButton.setOnClickListener {
            checkAndSubtractSugarAmount(sugarAmount)
        }

        updateTotalAmount()
        updateTotalSugarAmount()
    }

    private fun updateDrinkAmount(amount: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            drinkDao.insert(Drink(amount = amount))
            updateTotalAmount()
        }
    }

    private fun checkAndSubtractAmount(cupAmount: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val totalAmount = drinkDao.getTotalAmount() ?: 0
            if (totalAmount > 0) {
                val newAmount = totalAmount - cupAmount
                if (newAmount >= 0) {
                    drinkDao.insert(Drink(amount = -cupAmount))
                    withContext(Dispatchers.Main) {
                        updateTotalAmount()
                    }
                }
            }
        }
    }

    private fun updateSugarAmount(amount: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            drinkDao.insert(Drink(amount = 0, sugar = amount))
            updateTotalSugarAmount()
        }
    }

    private fun checkAndSubtractSugarAmount(sugarAmount: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val totalSugarAmount = drinkDao.getTotalSugarAmount() ?: 0
            if (totalSugarAmount > 0) {
                val newSugarAmount = totalSugarAmount - sugarAmount
                if (newSugarAmount >= 0) {
                    drinkDao.insert(Drink(amount = 0, sugar = -sugarAmount))
                    withContext(Dispatchers.Main) {
                        updateTotalSugarAmount()
                    }
                }
            }
        }
    }

    private fun updateTotalAmount() {
        lifecycleScope.launch(Dispatchers.IO) {
            val totalAmount = drinkDao.getTotalAmount() ?: 0
            withContext(Dispatchers.Main) {
                binding.totalDrinkAmountTextView.text = "$totalAmount ml"
            }
        }
    }

    private fun updateTotalSugarAmount() {
        lifecycleScope.launch(Dispatchers.IO) {
            val totalSugarAmount = drinkDao.getTotalSugarAmount() ?: 0
            withContext(Dispatchers.Main) {
                binding.totalSugarAmountTextView.text = "$totalSugarAmount g"
            }
        }
    }
}
