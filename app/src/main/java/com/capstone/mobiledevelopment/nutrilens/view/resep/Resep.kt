package com.capstone.mobiledevelopment.nutrilens.view.resep


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.addfood.AddFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.resep.adapter.ResepAdapter
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class Resep : AppCompatActivity() {

    private lateinit var resepAdapter: ResepAdapter
    private lateinit var resepList: List<ResepItem>
    private var currentPage = 0
    private val itemsPerPage = 20
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resep)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resepAdapter = ResepAdapter(mutableListOf())
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = resepAdapter

        // AsyncTask untuk memuat data JSON dari URL
        LoadRecipeDataTask(this) { loadedList ->
            resepList = loadedList
            loadMoreData()
        }.execute()

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

        // Initialize the custom bottom navigation view
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)

        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_food)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_stats -> {
                    val intent = Intent(this@Resep, MainActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_stats)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this@Resep, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    false
                }
                R.id.navigation_documents -> {
                    val intent = Intent(this@Resep, CatatanMakanan::class.java)
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

        // Add the FAB click listener
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@Resep, AddFoodActivity::class.java)
            startActivity(intent)
        }

        // Set up search view
        setupSearchView()
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

    private class LoadRecipeDataTask(
        context: Resep,
        private val callback: (List<ResepItem>) -> Unit
    ) : AsyncTask<Void, Void, List<ResepItem>>() {

        private val activityReference = WeakReference(context)

        override fun onPreExecute() {
            super.onPreExecute()
            activityReference.get()?.progressBar?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): List<ResepItem> {
            val activity = activityReference.get()
            return if (activity != null) {
                getRecipeDataFromUrl()
            } else {
                emptyList()
            }
        }

        override fun onPostExecute(result: List<ResepItem>) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if (activity != null) {
                callback(result)
                activity.progressBar.visibility = View.GONE
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
}
