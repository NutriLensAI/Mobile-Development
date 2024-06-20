package com.capstone.mobiledevelopment.nutrilens.view.resep

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepDatabase
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.reponse.RegisterResponse
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.RecommendedFood
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.UserProfileRequest
import com.capstone.mobiledevelopment.nutrilens.view.adapter.resep.ResepAdapter
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginActivity
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.main.MainViewModel
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.CustomBottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ResepActivity : AppCompatActivity() {

    private lateinit var resepAdapter: ResepAdapter
    private lateinit var resepList: List<ResepItem>
    private var currentPage = 0
    private val itemsPerPage = 20
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var db: StepDatabase
    private var isGuestUser: Boolean = false
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resep)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Room.databaseBuilder(
            applicationContext,
            StepDatabase::class.java, "nutrilens-db"
        ).build()

        resepAdapter = ResepAdapter(mutableListOf())
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = resepAdapter

        // Add the divider
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Load recipe data
        loadRecipeData()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (totalItemCount <= (lastVisibleItem + 5)) {
                    loadMoreData()
                }
            }
        })

        // Initialize bottom navigation view and FAB
        setupBottomNavigationView()
        setupFAB()
        setupSearchView()
        setupView()
        checkGuestUser()
        observeSession() // Add this line to observe session
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (user == null || !user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                user.token.let { token ->
                    if (!isGuestUser) {
                        viewModel.fetchUserProfile(token)
                        viewModel.userProfile.observe(this) { userProfile ->
                            userProfile?.let {
                                sendProfileDataToApi(it)
                            }
                        }
                    }
                }
            }
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

        RetrofitInstance.api.showRecommendedFoods(request)
            .enqueue(object : Callback<List<RecommendedFood>> {
                override fun onResponse(
                    call: Call<List<RecommendedFood>>,
                    response: Response<List<RecommendedFood>>
                ) {
                    if (response.isSuccessful) {
                        response.body()
                        // Handle the recommended foods list here
                    } else {
                        // Handle the error case
                    }
                }

                override fun onFailure(call: Call<List<RecommendedFood>>, t: Throwable) {
                    // Handle the failure case
                }
            })
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
        window.navigationBarColor =
            ContextCompat.getColor(this, android.R.color.white) // Set navigation bar color to white
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)

        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_food)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_stats -> {
                    val intent = Intent(this@ResepActivity, MainActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_stats)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this@ResepActivity, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    false
                }

                R.id.navigation_documents -> {
                    val intent = Intent(this@ResepActivity, CatatanMakanan::class.java)
                    intent.putExtra("selected_item", R.id.navigation_documents)
                    startActivity(intent)
                    true
                }

                R.id.navigation_food -> {
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId
    }

    private fun setupFAB() {
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (isGuestUser) {
                showLoginDialog()
            } else {
                val intent = Intent(this@ResepActivity, CameraFoodActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showLoginDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.login_required))
            .setMessage(getString(R.string.you_must_be_logged_in_to_use_this_feature))
            .setPositiveButton(R.string.login_now) { dialog, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
                dialog.dismiss()
            }
            .setNegativeButton("Later") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun checkGuestUser() {
        val userPreference = UserPreference.getInstance(dataStore)
        CoroutineScope(Dispatchers.Main).launch {
            val userModel = userPreference.getSession().first()
            isGuestUser = userModel.isGuest
            setupFAB() // Reinitialize FAB after checking guest user status
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterRecipes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterRecipes(it)
                }
                return true
            }
        })

        // Ensure the search view is expanded and focused by default
        searchView.isIconified = false
        searchView.clearFocus()
    }

    private fun filterRecipes(query: String) {
        val filteredList = resepList.filter { it.Title.contains(query, ignoreCase = true) }
        resepAdapter.updateRecipes(filteredList)
    }

    private fun loadMoreData() {
        val start = currentPage * itemsPerPage
        val end = start + itemsPerPage
        if (start < resepList.size) {
            progressBar.visibility = View.VISIBLE
            val nextPageItems = resepList.subList(start, minOf(end, resepList.size))
            resepAdapter.addRecipes(nextPageItems)
            currentPage++
            progressBar.visibility = View.GONE
        }
    }

    private fun loadRecipeData() {
        CoroutineScope(Dispatchers.Main).launch {
            progressBar.visibility = View.VISIBLE
            val loadedList = withContext(Dispatchers.IO) {
                getRecipeDataFromAsset()
            }
            resepList = loadedList
            loadMoreData()
            progressBar.visibility = View.GONE
        }
    }

    private fun getRecipeDataFromAsset(): List<ResepItem> {
        val jsonString: String
        try {
            val inputStream = assets.open("datarecipe.json")
            jsonString = inputStream.bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val gson = Gson()
        val recipeType = object : TypeToken<RecipeData>() {}.type
        val recipeData: RecipeData = gson.fromJson(jsonString, recipeType)
        return recipeData.recipeData
    }
}
