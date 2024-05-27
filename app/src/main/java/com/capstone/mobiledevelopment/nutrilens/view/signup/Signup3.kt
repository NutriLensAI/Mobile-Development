package com.capstone.mobiledevelopment.nutrilens.view.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.mobiledevelopment.nutrilens.R

class Signup3 : AppCompatActivity() {
    private lateinit var selectedGoals: MutableList<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectedGoals = mutableListOf()

        setupAction()
    }

    private fun setupAction() {
        val goalButtons = listOf(
            findViewById<Button>(R.id.goalButton1),
            findViewById<Button>(R.id.goalButton2),
            findViewById<Button>(R.id.goalButton3),
            findViewById<Button>(R.id.goalButton4),
            findViewById<Button>(R.id.goalButton5)
        )

        goalButtons.forEach { button ->
            button.setOnClickListener {
                if (selectedGoals.contains(button)) {
                    selectedGoals.remove(button)
                    button.isSelected = false
                } else {
                    if (selectedGoals.size < 3) {
                        selectedGoals.add(button)
                        button.isSelected = true
                    }
                }
            }
        }

        val continueButton = findViewById<Button>(R.id.continueButton)
        continueButton.setOnClickListener {
            val intent = Intent(this@Signup3, Signup4::class.java)
            startActivity(intent)
        }
    }
}