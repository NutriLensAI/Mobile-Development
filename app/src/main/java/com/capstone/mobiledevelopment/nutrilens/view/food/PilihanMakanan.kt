package com.capstone.mobiledevelopment.nutrilens.view.food

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter2
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodResponse
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class PilihanMakanan : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter2: FoodAdapter2
    private lateinit var allFoodList: List<FoodResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilihan_makanan)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        foodAdapter2 = FoodAdapter2(emptyList())
        recyclerView.adapter = foodAdapter2

        fetchFoodData()

        val searchBar: EditText = findViewById(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
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

    private fun filter(text: String) {
        val filteredList = allFoodList.filter {
            it.name.contains(text, ignoreCase = true)
        }
        foodAdapter2.updateList(filteredList)
    }
}
