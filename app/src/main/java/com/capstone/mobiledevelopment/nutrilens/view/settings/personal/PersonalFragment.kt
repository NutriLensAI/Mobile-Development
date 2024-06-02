package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.mobiledevelopment.nutrilens.R

class PersonalFragment : Fragment() {
    companion object {
        fun newInstance(weight: String, height: String, age: String, gender: String) = PersonalFragment().apply {
            arguments = Bundle().apply {
                putString("weight", weight)
                putString("height", height)
                putString("age", age)
                putString("gender", gender)
            }
        }
    }

    private var weight: String? = null
    private var height: String? = null
    private var age: String? = null
    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weight = it.getString("weight")
            height = it.getString("height")
            age = it.getString("age")
            gender = it.getString("gender")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        // Set the data to the TextViews
        view.findViewById<TextView>(R.id.tvWeight).text = weight
        view.findViewById<TextView>(R.id.tvHeight).text = height
        view.findViewById<TextView>(R.id.tvAge).text = age
        view.findViewById<TextView>(R.id.tvGender).text = gender

        val btnChangePersonalData = view.findViewById<Button>(R.id.btnChangePersonalData)
        btnChangePersonalData.setOnClickListener {
            navigateToFragment(InputDataFragment.newInstance())
        }

        return view
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)  // Optional: Adds the transaction to the backstack
            .commit()
    }
}