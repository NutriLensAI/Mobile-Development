package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.settings.personal.changepersonal.PersonalActivity

class PersonalFragment : Fragment() {

    companion object {
        fun newInstance() = PersonalFragment()
    }

    private val viewModel: PersonalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        val btnChangePersonalData = view.findViewById<Button>(R.id.btnChangePersonalData)
        btnChangePersonalData.setOnClickListener {
            val intent = Intent(fragment, PersonalActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}