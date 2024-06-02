package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.settings.password.PasswordFragment

class InputDataFragment : Fragment() {
    private val personalViewModel: PersonalViewModel by activityViewModels()

    companion object {
        fun newInstance() = PasswordFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_input_data, container, false)
        view.findViewById<Button>(R.id.buttonSave).setOnClickListener {
            saveDataToViewModel(view)
            navigateToPersonalFragment()
        }
        return view
    }

    private fun saveDataToViewModel(view: View) {
        val weight = view.findViewById<EditText>(R.id.tiWeight).text.toString()
        val height = view.findViewById<EditText>(R.id.tiHeight).text.toString()
        val age = view.findViewById<EditText>(R.id.tiAge).text.toString()
        val gender = view.findViewById<Spinner>(R.id.spinnerGender).selectedItem.toString()
        personalViewModel.saveUserData(weight, height, age, gender)
    }

    private fun navigateToPersonalFragment() {
        val personalFragment = PersonalFragment.newInstance()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, personalFragment)
            ?.commit()
    }
}