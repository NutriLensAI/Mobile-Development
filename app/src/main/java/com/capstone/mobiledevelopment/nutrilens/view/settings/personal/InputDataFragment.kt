package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.FragmentInputDataBinding
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory

class InputDataFragment : Fragment() {

    private var _binding: FragmentInputDataBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null

    private val viewModel: PersonalViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupView()
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            val activity = binding.spinnerActivityLevel.selectedItem.toString()
            val weight = binding.tiWeight.text.toString()
            val height = binding.tiHeight.text.toString()
            val age = binding.tiAge.text.toString()
            val gender = binding.spinnerGender.selectedItem.toString()

            if (validateInput(weight, height, age)) {
                token?.let {
                    viewModel.saveUserData(activity, weight, height, age, gender)
                    viewModel.updateProfileData(it) { success, message ->
                        if (success) {
                            Log.d("InputDataFragment", "Profile updated successfully: $message")
                            // Optionally, go back to PersonalFragment to see the updated data
                            parentFragmentManager.popBackStack()
                        } else {
                            Log.e("InputDataFragment", "Failed to update profile: $message")
                            // Handle failed profile update
                        }
                    }
                } ?: run {
                    Log.e("InputDataFragment", "Token is null")
                    // Handle token fetch failure
                }
            } else {
                Log.e("InputDataFragment", "Invalid input")
                // Handle invalid input
            }
        }
    }

    private fun validateInput(weight: String, height: String, age: String): Boolean {
        // Add input validation logic if needed
        return weight.isNotEmpty() && height.isNotEmpty() && age.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TOKEN = "token"

        fun newInstance(token: String) = InputDataFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TOKEN, token)
            }
        }
    }

    private fun setupView() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true // Set status bar content to dark
                controller.isAppearanceLightNavigationBars = true // Set navigation bar content to dark
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green)
            window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white) // Change navigation bar color
        }
    }
}