package com.capstone.mobiledevelopment.nutrilens.view.catatan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.addstory.AddFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.customview.CustomBottomNavigationView
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.pilihan.PilihanMakanan
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CatatanMakanan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catatan_makanan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAdd: ImageButton = findViewById(R.id.btnAdd1)
        btnAdd.setOnClickListener {
            val intent = Intent(this, PilihanMakanan::class.java)
            startActivity(intent)
        }

        val btnAdd1: ImageButton = findViewById(R.id.btnAdd2)
        btnAdd1.setOnClickListener {
            val intent = Intent(this, PilihanMakanan::class.java)
            startActivity(intent)
        }

        val btnAdd3: ImageButton = findViewById(R.id.btnAdd3)
        btnAdd3.setOnClickListener {
            val intent = Intent(this, PilihanMakanan::class.java)
            startActivity(intent)
        }

        // Initialize the custom bottom navigation view
        val bottomNavigationView = findViewById<CustomBottomNavigationView>(R.id.customBottomBar)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)

        val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_stats)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_food -> {
                    val intent = Intent(this@CatatanMakanan, PilihanMakanan::class.java)
                    intent.putExtra("selected_item", R.id.navigation_food)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this@CatatanMakanan, SettingsActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_profile)
                    startActivity(intent)
                    true
                }

                R.id.navigation_stats -> {
                    val intent = Intent(this@CatatanMakanan, MainActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_stats)
                    startActivity(intent)
                    true
                }

                R.id.navigation_documents -> {
                    // Activity ini sudah halaman statistik
                    true
                }

                else -> false
            }
        }

        // Add the FAB click listener
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@CatatanMakanan, AddFoodActivity::class.java)
            startActivity(intent)
        }
    }
}