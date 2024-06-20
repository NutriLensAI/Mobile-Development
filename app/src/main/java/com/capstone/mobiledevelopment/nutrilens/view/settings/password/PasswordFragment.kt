package com.capstone.mobiledevelopment.nutrilens.view.settings.password

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.FragmentPasswordBinding
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsFragment
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory

class PasswordFragment : Fragment() {

    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null

    private val viewModel: PasswordViewModel by viewModels {
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
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupView()
    }

    private fun setupListeners() {
        binding.btnUpdatePassword.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmNewPassword.text.toString()

            if (newPassword.length < 8) {
                Toast.makeText(
                    requireContext(),
                    "Password must be at least 8 characters long!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (newPassword == confirmPassword) {
                Log.d("PasswordFragment", "Updating password with token: $token")
                token?.let {
                    viewModel.updatePassword(newPassword, it) { success, message ->
                        if (success) {
                            Log.d("PasswordFragment", "Password updated successfully: $message")
                            Toast.makeText(
                                requireContext(),
                                "Change Password Success!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToSettingsFragment()
                        } else {
                            Log.e("PasswordFragment", "Failed to update password: $message")
                            Toast.makeText(
                                requireContext(),
                                "Change Password Failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } ?: run {
                    Log.e("PasswordFragment", "Token is null")
                    Toast.makeText(requireContext(), "Change Password Failed!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Log.e("PasswordFragment", "Password mismatch")
                Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun navigateToSettingsFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TOKEN = "token"

        fun newInstance(token: String) = PasswordFragment().apply {
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
                controller.isAppearanceLightNavigationBars =
                    true // Set navigation bar content to dark
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green)
            window.navigationBarColor = ContextCompat.getColor(
                requireContext(),
                R.color.white
            ) // Change navigation bar color
        }
    }
}
