package com.capstone.mobiledevelopment.nutrilens.view.food

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.Food
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter2
import android.widget.AdapterView
import android.view.View
import android.widget.Spinner
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan

class FoodChoice : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter2: FoodAdapter2
    private lateinit var selectedMealType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilihan_makanan)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup Spinner
        val mealTypeSpinner: Spinner = findViewById(R.id.meal_type_spinner)
        mealTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMealType = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedMealType = ""
            }
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create dummy data
        val foodList = listOf(
            Food("Ayam Bakar", 200, 10, 5, 20),
            Food("Nasi Goreng", 300, 50, 10, 10),
            Food("Salad Buah", 150, 25, 2, 3)
        )

        foodAdapter2 = FoodAdapter2(foodList) { food ->
            if (selectedMealType.isNotEmpty()) {
                val intent = Intent(this, CatatanMakanan::class.java).apply {
                    putExtra("meal_type", selectedMealType)
                    putExtra("nama_makanan", food.name)
                    putExtra("calories", food.calories)
                    putExtra("carbs", food.carbs)
                    putExtra("fat", food.fat)
                    putExtra("protein", food.protein)
                }
                startActivity(intent)
            }
        }
        recyclerView.adapter = foodAdapter2
    }
}
