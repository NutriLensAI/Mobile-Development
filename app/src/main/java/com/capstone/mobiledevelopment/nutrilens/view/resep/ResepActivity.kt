package com.capstone.mobiledevelopment.nutrilens.view.resep

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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
import com.capstone.mobiledevelopment.nutrilens.view.adapter.resep.ResepAdapter
import com.capstone.mobiledevelopment.nutrilens.view.camera.CameraFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginActivity
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.capstone.mobiledevelopment.nutrilens.view.utils.customview.CustomBottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ResepActivity : AppCompatActivity() {

    private lateinit var resepAdapter: ResepAdapter
    private lateinit var resepList: List<ResepItem>
    private var currentPage = 0
    private val itemsPerPage = 20
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var db: StepDatabase
    private var isGuestUser: Boolean = false

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
    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars =
                true // Optional: Set status bar content to dark
        }
        supportActionBar?.hide()

        // Set status bar color to green
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)
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
            .setTitle("Login Required")
            .setMessage("You must be logged in to use this feature.")
            .setPositiveButton("Login Now") { dialog, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
                dialog.dismiss()
            }
            .setNegativeButton("Later") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
                getRecipeDataFromUrl()
            }
            resepList = loadedList
            loadMoreData()
            progressBar.visibility = View.GONE
        }
    }

    private fun getRecipeDataFromUrl(): List<ResepItem> {
        val urlString = "https://nutrilensai.github.io/datadummy/datarecipe.json"
        var jsonString: String
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            jsonString = connection.inputStream.bufferedReader().use { it.readText() }
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
