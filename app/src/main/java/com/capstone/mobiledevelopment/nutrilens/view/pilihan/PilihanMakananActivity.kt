package com.capstone.mobiledevelopment.nutrilens.view.pilihan

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.favorite.FavoriteRecipe
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.FoodRequest
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.RecommendedFood
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.UserProfileRequest
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FavoriteRecipeAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.PilihanFoodAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.AddMyRecipes
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipe
import com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes.MyRecipesAdapter
import com.capstone.mobiledevelopment.nutrilens.view.main.MainViewModel
import com.capstone.mobiledevelopment.nutrilens.view.resep.RetrofitInstance
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PilihanMakananActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pilihanFoodAdapter: PilihanFoodAdapter
    private lateinit var favoriteRecipeAdapter: FavoriteRecipeAdapter
    private lateinit var myRecipesAdapter: MyRecipesAdapter
    private var allFoodList: MutableList<RecommendedFood> = mutableListOf()
    private lateinit var favoriteFoodList: MutableList<FavoriteRecipe>
    private lateinit var myRecipeList: MutableList<MyRecipe>
    private lateinit var db: StepDatabase
    private lateinit var fabAddRecipe: FloatingActionButton
    private lateinit var userPreference: UserPreference
    private lateinit var mealTypeSpinner: Spinner
    private var token: String = ""
    private var selectedTable: String = "breakfasts" // Default value

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilihan_makanan)

        db = StepDatabase.getDatabase(applicationContext)
        userPreference = UserPreference.getInstance(dataStore)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pilihanFoodAdapter = PilihanFoodAdapter(emptyList()) { food ->
            sendFoodData(selectedTable, food)
        }
        favoriteRecipeAdapter = FavoriteRecipeAdapter(emptyList(), this)
        myRecipesAdapter = MyRecipesAdapter(emptyList(), this::deleteRecipe)
        recyclerView.adapter = pilihanFoodAdapter

        fabAddRecipe = findViewById(R.id.fab_add_recipe)
        fabAddRecipe.setOnClickListener {
            val intent = Intent(this@PilihanMakananActivity, AddMyRecipes::class.java)
            startActivity(intent)
        }

        mealTypeSpinner = findViewById(R.id.meal_type_spinner)
        setupSpinner()

        observeSession()
        setupSearchBar()
        setupTabLayout()
    }

    private fun observeSession() {
        lifecycleScope.launch {
            val session = userPreference.getSession().first()
            token = session.token
            viewModel.fetchUserProfile(token)
            viewModel.userProfile.observe(this@PilihanMakananActivity) { userProfile ->
                userProfile?.let {
                    sendProfileDataToApi(it)
                }
            }
            fetchFoodData()
        }
    }

    private fun sendProfileDataToApi(userProfile: RegisterResponse) {
        val request = UserProfileRequest(
            weight_kg = userProfile.weight ?: 0,
            height_cm = userProfile.height ?: 0,
            age_years = userProfile.age ?: 0,
            gender = userProfile.gender ?: "",
            activity_level = userProfile.activityLevel ?: ""
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.showRecommendedFoods(request).execute()
                // Update the food list with recommended foods
                response.body()?.let { recommendedFoods ->
                    withContext(Dispatchers.Main) {
                        allFoodList.addAll(0, recommendedFoods) // Add recommended foods at the top
                        pilihanFoodAdapter.updateList(allFoodList)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error here, maybe show a Toast message
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PilihanMakananActivity, "Failed to fetch recommended foods", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                // Handle error here, maybe show a Toast message
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PilihanMakananActivity, "Failed to fetch recommended foods", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSpinner() {
        val mealTypes = arrayOf("breakfasts", "lunchs", "dinners")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealTypeSpinner.adapter = adapter

        mealTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTable = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Default value or any action if needed
            }
        }
    }

    private fun fetchFoodData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiConfig.getApiService().getFoodData()
                withContext(Dispatchers.Main) {
                    val additionalFoods = response.map {
                        RecommendedFood(
                            name = it.name,
                            calories = it.calories,
                            proteins = it.proteins,
                            fat = it.fat,
                            carbohydrate = it.carbohydrate
                        )
                    }
                    allFoodList.addAll(additionalFoods)
                    pilihanFoodAdapter.updateList(allFoodList)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error here, maybe show a Toast message
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PilihanMakananActivity, "Failed to fetch food data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendFoodData(table: String, food: RecommendedFood) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val foodRequest = FoodRequest(
                    id = 0, // Biarkan server yang menetapkan ID
                    user_id = 0, // Ambil user_id dari session
                    food_id = 0, // Assuming there's no ID for RecommendedFood
                    food_name = food.name,
                    calories = food.calories,
                    proteins = food.proteins, // Pastikan field ini ada di FoodResponse
                    fat = food.fat, // Pastikan field ini ada di FoodResponse
                    carbohydrate = food.carbohydrate // Pastikan field ini ada di FoodResponse
                )

                val response =
                    ApiConfig.getApiService().addFoodToMeal(token, table, 0, foodRequest)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PilihanMakananActivity,
                        "Food added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PilihanMakananActivity,
                        "Failed to add food",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error here, maybe show a Toast message
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PilihanMakananActivity, "Failed to add food", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchFavoriteRecipes() {
        lifecycleScope.launch(Dispatchers.IO) {
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
        lifecycleScope.launch(Dispatchers.IO) {
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
        pilihanFoodAdapter.updateList(filteredList)
    }

    private fun setupTabLayout() {
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        recyclerView.adapter = pilihanFoodAdapter
                        pilihanFoodAdapter.updateList(allFoodList)
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
        lifecycleScope.launch(Dispatchers.IO) {
            db.myRecipeDao().deleteRecipe(recipe)
            fetchMyRecipes()
        }
    }
}
