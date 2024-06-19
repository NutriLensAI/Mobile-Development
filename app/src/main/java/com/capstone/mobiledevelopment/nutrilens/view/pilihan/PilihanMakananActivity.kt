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
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
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
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
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
    private lateinit var allFoodList: List<FoodResponse>
    private lateinit var recommendedFoodList: List<FoodResponse>
    private lateinit var favoriteFoodList: MutableList<FavoriteRecipe>
    private lateinit var myRecipeList: MutableList<MyRecipe>
    private lateinit var db: StepDatabase
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

        mealTypeSpinner = findViewById(R.id.meal_type_spinner)
        setupSpinner()

        observeSession()
        setupSearchBar()
        setupTabLayout()
        setupView()
    }

    private fun getDrawableResourceId(index: Int): Int {
        return when (index) {
            0 -> R.drawable.drawable_1
            1 -> R.drawable.drawable_2
            2 -> R.drawable.drawable_3
            3 -> R.drawable.drawable_4
            4 -> R.drawable.drawable_5
            5 -> R.drawable.drawable_6
            6 -> R.drawable.drawable_7
            7 -> R.drawable.drawable_8
            8 -> R.drawable.drawable_9
            9 -> R.drawable.drawable_10
            else -> R.drawable.image_9 // default drawable in case of index > 9
        }
    }

    private fun convertToFoodResponse(recommendedFoods: List<RecommendedFood>): List<FoodResponse> {
        return recommendedFoods.mapIndexed { index, it ->
            FoodResponse(
                id = index, // Memberikan id default
                name = "${it.name} (Rekomendasi)", // Menambahkan teks rekomendasi
                calories = it.calories,
                image = getDrawableResourceId(index).toString(), // Menetapkan gambar dari drawable
                proteins = it.proteins,
                fat = it.fat,
                carbohydrate = it.carbohydrate,
                isRecommended = true // Menetapkan isRecommended ke true
            )
        }
    }


    private fun updateRecommendedFoodList(recommendedFoods: List<RecommendedFood>) {
        // Convert RecommendedFood to FoodResponse
        val foodResponses = convertToFoodResponse(recommendedFoods)
        recommendedFoodList = foodResponses
        // Jika allFoodList sudah diinisialisasi, gabungkan list makanan yang direkomendasikan dengan semua makanan, rekomendasi di atas
        if (::allFoodList.isInitialized) {
            val combinedList = foodResponses + allFoodList
            pilihanFoodAdapter.updateList(combinedList)
        }
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
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { recommendedFoods ->
                            updateRecommendedFoodList(recommendedFoods)
                        }
                    } else {
                        Toast.makeText(
                            this@PilihanMakananActivity,
                            "Failed to fetch recommended foods",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PilihanMakananActivity,
                        "Failed to fetch recommended foods",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PilihanMakananActivity,
                        "Failed to fetch recommended foods",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    allFoodList = response
                    // Jika sudah ada data rekomendasi, update list makanan dengan menggabungkannya
                    if (::recommendedFoodList.isInitialized) {
                        val combinedList = recommendedFoodList + allFoodList
                        pilihanFoodAdapter.updateList(combinedList)
                    } else {
                        pilihanFoodAdapter.updateList(allFoodList)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PilihanMakananActivity,
                        "Failed to fetch food data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
            controller.isAppearanceLightStatusBars = true // Set status bar content to dark
            controller.isAppearanceLightNavigationBars = true // Set navigation bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to white
        window.statusBarColor = ContextCompat.getColor(this, R.color.green2)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.white) // Set navigation bar color to white
    }

    private fun sendFoodData(table: String, food: FoodResponse) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val foodRequest = FoodRequest(
                    id = 0, // Let the server assign the ID
                    user_id = 0, // Get user_id from session
                    food_id = food.id,
                    food_name = food.name,
                    calories = food.calories,
                    proteins = food.proteins,
                    fat = food.fat,
                    carbohydrate = food.carbohydrate
                )

                ApiConfig.getApiService().addFoodToMeal(token, table, food.id, foodRequest)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PilihanMakananActivity,
                        "Food added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Finish this activity and return to CatatanMakananActivity
                    finish()
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
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PilihanMakananActivity,
                        "Failed to add food",
                        Toast.LENGTH_SHORT
                    ).show()
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
                        if (::recommendedFoodList.isInitialized) {
                            val combinedList = recommendedFoodList + allFoodList
                            pilihanFoodAdapter.updateList(combinedList)
                        } else {
                            pilihanFoodAdapter.updateList(allFoodList)
                        }
                    }

                    1 -> {
                        recyclerView.adapter = favoriteRecipeAdapter
                        fetchFavoriteRecipes()
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
