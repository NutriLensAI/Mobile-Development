package com.capstone.mobiledevelopment.nutrilens.view.resep

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FavoriteRecipeAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter2
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.RetrofitInstance
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.AddMyRecipes
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipe
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipesAdapter
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class PilihanMakanan : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter2: FoodAdapter2
    private lateinit var favoriteRecipeAdapter: FavoriteRecipeAdapter
    private lateinit var myRecipesAdapter: MyRecipesAdapter
    private lateinit var allFoodList: List<FoodResponse>
    private lateinit var favoriteFoodList: MutableList<FavoriteRecipe>
    private lateinit var myRecipeList: MutableList<MyRecipe>
    private lateinit var db: StepDatabase
    private lateinit var fabAddRecipe: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilihan_makanan)

        db = StepDatabase.getDatabase(applicationContext)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        foodAdapter2 = FoodAdapter2(emptyList())
        favoriteRecipeAdapter = FavoriteRecipeAdapter(emptyList(), this)
        myRecipesAdapter = MyRecipesAdapter(emptyList(), this::deleteRecipe)
        recyclerView.adapter = foodAdapter2

        fabAddRecipe = findViewById(R.id.fab_add_recipe)
        fabAddRecipe.setOnClickListener {
            val intent = Intent(this@PilihanMakanan, AddMyRecipes::class.java)
            startActivity(intent)
        }

        fetchFoodData()
        setupSearchBar()
        setupTabLayout()
    }

    private fun fetchFoodData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getFoodData()
                withContext(Dispatchers.Main) {
                    allFoodList = response
                    foodAdapter2.updateList(allFoodList)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error here, maybe show a Toast message
            }
        }
    }

    private fun fetchFavoriteRecipes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favoriteRecipes = db.favoriteRecipeDao().getAllFavorites()
                withContext(Dispatchers.Main) {
                    favoriteFoodList = favoriteRecipes.toMutableList()
                    favoriteRecipeAdapter.updateList(favoriteFoodList)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error here, maybe show a Toast message
            }
        }
    }

    private fun fetchMyRecipes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val myRecipes = db.myRecipeDao().getAllRecipes()
                withContext(Dispatchers.Main) {
                    myRecipeList = myRecipes.toMutableList()
                    myRecipesAdapter.updateList(myRecipeList)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error here, maybe show a Toast message
            }
        }
    }

    private fun setupSearchBar() {
        val searchBar: EditText = findViewById(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(text: String) {
        val filteredList = allFoodList.filter {
            it.name.contains(text, ignoreCase = true)
        }
        foodAdapter2.updateList(filteredList)
    }

    private fun setupTabLayout() {
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        recyclerView.adapter = foodAdapter2
                        foodAdapter2.updateList(allFoodList)
                        fabAddRecipe.visibility = View.GONE
                    }
                    1 -> {
                        recyclerView.adapter = favoriteRecipeAdapter
                        fetchFavoriteRecipes()
                        fabAddRecipe.visibility = View.GONE
                    }
                    2 -> {
                        recyclerView.adapter = myRecipesAdapter
                        fetchMyRecipes()
                        fabAddRecipe.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun deleteRecipe(recipe: MyRecipe) {
        CoroutineScope(Dispatchers.IO).launch {
            db.myRecipeDao().deleteRecipe(recipe)
            fetchMyRecipes()
        }
    }
}
