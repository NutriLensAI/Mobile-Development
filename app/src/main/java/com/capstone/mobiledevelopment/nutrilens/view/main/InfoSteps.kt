package com.capstone.mobiledevelopment.nutrilens.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mobiledevelopment.nutrilens.R

class InfoSteps : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_steps)
        supportActionBar?.title = "Info Steps"
    }
}
