package com.capstone.mobiledevelopment.nutrilens.view.catatan.input

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.breakfast.BreakfastFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.dinner.DinnerFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.drink.DrinkFragment
import com.capstone.mobiledevelopment.nutrilens.view.catatan.input.lunch.LunchFragment

class InputCatatanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_catatan) // Ensure this layout has a container for fragments

        if (savedInstanceState == null) {
            val selectedFragment = intent.getStringExtra("selected_fragment") ?: "BREAKFAST"
            val fragment = when (selectedFragment) {
                "BREAKFAST" -> BreakfastFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("selected_meal", intent.getParcelableExtra("selected_meal"))
                    }
                }

                "LUNCH" -> LunchFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("selected_meal", intent.getParcelableExtra("selected_meal"))
                    }
                }

                "DINNER" -> DinnerFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("selected_meal", intent.getParcelableExtra("selected_meal"))
                    }
                }

                "DRINK" -> DrinkFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("selected_meal", intent.getParcelableExtra("selected_meal"))
                    }
                }

                else -> BreakfastFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("selected_meal", intent.getParcelableExtra("selected_meal"))
                    }
                }
            }
            supportFragmentManager.commit {
                replace(R.id.fragment_container, fragment)
                setReorderingAllowed(true)
            }
        }
    }
}