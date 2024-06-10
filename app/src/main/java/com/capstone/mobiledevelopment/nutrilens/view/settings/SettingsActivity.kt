package com.capstone.mobiledevelopment.nutrilens.view.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.capstone.mobiledevelopment.nutrilens.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            // We keep the mechanism to handle different item selections
            val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_profile)  // Default or fallback item
            val fragment = when (selectedItemId) {
                R.id.navigation_profile -> SettingsFragment.newInstance()  // Using new instance without parameters
                else -> SettingsFragment.newInstance()  // Default case also leads to SettingsFragment without parameters
            }
            supportFragmentManager.commit {
                replace(R.id.fragment_container, fragment)
                setReorderingAllowed(true)
            }
        }
    }
}