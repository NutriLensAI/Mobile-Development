package com.capstone.mobiledevelopment.nutrilens.view.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PilihanMakanan : AppCompatActivity() {
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
                val selectedMealType = parent.getItemAtPosition(position).toString()
                // Handle the selection of meal type here
                // For example, update the UI or fetch data based on the selected meal type
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no item is selected, if needed
            }
        }

        // Setup BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_food)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_stats -> {
                    val intent = Intent(this@PilihanMakanan, MainActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_stats)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this@PilihanMakanan, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    true
                }
                R.id.navigation_documents -> {
                    val intent = Intent(this@PilihanMakanan, CatatanMakanan::class.java)
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
}
