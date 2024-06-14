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
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory

class PersonalFragment : Fragment() {

    companion object {
        fun newInstance(token: String) = PersonalFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TOKEN, token)
            }
        }

        private const val ARG_TOKEN = "token"
    }

    private val personalViewModel: PersonalViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        personalViewModel.userData.observe(viewLifecycleOwner) { userData ->
            view.findViewById<TextView>(R.id.tvActivityLevel).text = userData.activity
            view.findViewById<TextView>(R.id.tvWeight).text = userData.weight
            view.findViewById<TextView>(R.id.tvHeight).text = userData.height
            view.findViewById<TextView>(R.id.tvAge).text = userData.age
            view.findViewById<TextView>(R.id.tvGender).text = userData.gender
        }

        view.findViewById<Button>(R.id.btnChangePersonalData).setOnClickListener {
            token?.let {
                navigateToFragment(InputDataFragment.newInstance(it))
            }
        }

        token?.let {
            personalViewModel.fetchUserPersonalData(it)
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