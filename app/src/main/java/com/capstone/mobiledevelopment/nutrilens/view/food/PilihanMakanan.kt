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
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.tabs.TabLayout

class PilihanMakanan : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter2: FoodAdapter2
    private lateinit var selectedMealType: String
    private lateinit var allFoodList: List<Food>
    private lateinit var favoriteFoodList: List<Food>
    private lateinit var myRecipesList: List<Food>

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
        allFoodList = listOf(
            Food("Ayam Bakar", 200, 10, 5, 20),
            Food("Nasi Goreng", 300, 50, 10, 10),
            Food("Salad Buah", 150, 25, 2, 3)
        )

        favoriteFoodList = listOf(
            Food("Ayam Bakar", 200, 10, 5, 20)
        )

        myRecipesList = listOf(
            Food("Nasi Goreng", 300, 50, 10, 10)
        )

        foodAdapter2 = FoodAdapter2(allFoodList) { food ->
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

        // Setup Search Bar
        val searchBar: EditText = findViewById(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Setup Tab Layout
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> updateRecyclerView(allFoodList)
                    1 -> updateRecyclerView(favoriteFoodList)
                    2 -> updateRecyclerView(myRecipesList)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun filter(text: String) {
        val filteredList = when {
            findViewById<TabLayout>(R.id.tab_layout).selectedTabPosition == 1 -> favoriteFoodList
            findViewById<TabLayout>(R.id.tab_layout).selectedTabPosition == 2 -> myRecipesList
            else -> allFoodList
        }.filter {
            it.name.contains(text, ignoreCase = true)
        }
        foodAdapter2.updateList(filteredList)
    }

    private fun updateRecyclerView(newList: List<Food>) {
        foodAdapter2.updateList(newList)
    }
}
