package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.settings.password.PasswordFragment

class PersonalFragment : Fragment() {
    companion object {
        fun newInstance() = PersonalFragment()
    }

    private val personalViewModel: PersonalViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        personalViewModel.userData.observe(viewLifecycleOwner) { userData ->
            view.findViewById<TextView>(R.id.tvActivityLevel).text = userData.activity ?: "Not set"
            view.findViewById<TextView>(R.id.tvWeight).text = userData.weight ?: "Not set"
            view.findViewById<TextView>(R.id.tvHeight).text = userData.height ?: "Not set"
            view.findViewById<TextView>(R.id.tvAge).text = userData.age ?: "Not set"
            view.findViewById<TextView>(R.id.tvGender).text = userData.gender ?: "Not set"
        }

        view.findViewById<Button>(R.id.btnChangePersonalData).setOnClickListener {
            navigateToFragment(InputDataFragment.newInstance())
        }
        return view
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}