package com.capstone.mobiledevelopment.nutrilens.view.catatan.input

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.breakfast.BreakfastFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.dinner.DinnerFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.drink.DrinkFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.lunch.LunchFragment
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsFragment

class InputCatatanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            val selectedFragment = intent.getStringExtra("selected_fragment") ?: "BREAKFAST"
            val fragment = when (selectedFragment) {
                "BREAKFAST" -> BreakfastFragment()
                "LUNCH" -> LunchFragment()
                "DINNER" -> DinnerFragment()
                "DRINK" -> DrinkFragment()
                else -> BreakfastFragment()
            }
            supportFragmentManager.commit {
                replace(R.id.fragment_container, fragment)
                setReorderingAllowed(true)
            }
        }
    }
}