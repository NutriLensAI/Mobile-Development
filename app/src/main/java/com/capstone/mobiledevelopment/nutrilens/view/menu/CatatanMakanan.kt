package com.capstone.mobiledevelopment.nutrilens.view.menu

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.addstory.AddFoodActivity
import com.capstone.mobiledevelopment.nutrilens.view.main.MainActivity
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
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

                R.id.navigation_add -> {
                    val intent = Intent(this@CatatanMakanan, AddFoodActivity::class.java)
                    intent.putExtra("selected_item", R.id.navigation_add)
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
    }
}