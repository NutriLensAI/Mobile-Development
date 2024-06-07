package com.capstone.mobiledevelopment.nutrilens.view.food

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.Food
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter2
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FavoriteRecipeAdapter
import android.widget.AdapterView
import android.view.View
import android.widget.Spinner
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipesAdapter
import com.capstone.mobiledevelopment.nutrilens.data.database.step.AppDatabase
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.AddMyRecipes
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipe
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PilihanMakanan : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter2: FoodAdapter2
    private lateinit var favoriteRecipeAdapter: FavoriteRecipeAdapter
    private lateinit var myRecipesAdapter: MyRecipesAdapter
    private lateinit var selectedMealType: String
    private lateinit var allFoodList: List<Food>
    private lateinit var favoriteFoodList: MutableList<FavoriteRecipe>
    private lateinit var myRecipesList: MutableList<MyRecipe>
    private lateinit var db: AppDatabase
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilihan_makanan)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "nutrilens-db"
        ).build()

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

        loadFavoriteRecipes()
        loadMyRecipes()

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

        favoriteRecipeAdapter = FavoriteRecipeAdapter(emptyList(), this)
        myRecipesAdapter = MyRecipesAdapter(emptyList())

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
                    0 -> {
                        recyclerView.adapter = foodAdapter2
                        updateRecyclerView(allFoodList)
                        fab.hide()
                    }
                    1 -> {
                        recyclerView.adapter = favoriteRecipeAdapter
                        updateFavoriteRecyclerView(favoriteFoodList)
                        fab.hide()
                    }
                    2 -> {
                        recyclerView.adapter = myRecipesAdapter
                        updateMyRecipesRecyclerView(myRecipesList)
                        fab.show()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Setup Floating Action Button
        fab = findViewById(R.id.fab_add_recipe)
        fab.hide()
        fab.setOnClickListener {
            val intent = Intent(this, AddMyRecipes::class.java)
            startActivity(intent)
        }
    }

    private fun filter(text: String) {
        val filteredList = when {
            findViewById<TabLayout>(R.id.tab_layout).selectedTabPosition == 1 -> favoriteFoodList.map {
                Food(it.title, 0, 0, 0, 0)  // Adjust according to your data
            }
            findViewById<TabLayout>(R.id.tab_layout).selectedTabPosition == 2 -> myRecipesList.map {
                Food(it.title, 0, 0, 0, 0)  // Adjust according to your data
            }
            else -> allFoodList
        }.filter {
            it.name.contains(text, ignoreCase = true)
        }
        foodAdapter2.updateList(filteredList)
    }

    private fun updateRecyclerView(newList: List<Food>) {
        foodAdapter2.updateList(newList)
    }

    private fun updateFavoriteRecyclerView(newList: List<FavoriteRecipe>) {
        favoriteRecipeAdapter.updateList(newList)
    }

    private fun updateMyRecipesRecyclerView(newList: List<MyRecipe>) {
        myRecipesAdapter.updateList(newList)
    }

    private fun loadFavoriteRecipes() {
        CoroutineScope(Dispatchers.Main).launch {
            val favoriteRecipes = withContext(Dispatchers.IO) {
                db.favoriteRecipeDao().getAllFavorites()
            }
            favoriteFoodList = favoriteRecipes.toMutableList()
            updateFavoriteRecyclerView(favoriteFoodList)
        }
    }

    private fun loadMyRecipes() {
        CoroutineScope(Dispatchers.Main).launch {
            val myRecipes = withContext(Dispatchers.IO) {
                db.myRecipeDao().getAllRecipes()
            }
            myRecipesList = myRecipes.toMutableList()
            updateMyRecipesRecyclerView(myRecipesList)
        }
    }
}
