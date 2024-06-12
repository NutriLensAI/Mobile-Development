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
            val selectedItemId = intent.getIntExtra("selected_item", R.id.navigation_profile)
            val navigateTo = intent.getStringExtra("navigate_to")
            val fragment = SettingsFragment.newInstance(selectedItemId, navigateTo)
            supportFragmentManager.commit {
                replace(R.id.fragment_container, fragment)
                setReorderingAllowed(true)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}