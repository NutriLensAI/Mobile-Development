package com.capstone.mobiledevelopment.nutrilens.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R

class InfoDrink : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_drink)
        supportActionBar?.title = "Info Drink"
    }
}
