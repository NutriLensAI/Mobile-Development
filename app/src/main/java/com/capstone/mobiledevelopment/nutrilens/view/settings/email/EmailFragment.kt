package com.capstone.mobiledevelopment.nutrilens.view.settings.email

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
import com.capstone.mobiledevelopment.nutrilens.databinding.FragmentEmailBinding
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory

class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null

    private val viewModel: EmailViewModel by viewModels {
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
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupView()
    }

    private fun setupListeners() {
        binding.btnUpdateEmail.setOnClickListener {
            val newEmail = binding.etNewEmail.text.toString()
            val confirmEmail = binding.etConfirmNewEmail.text.toString()

            if (newEmail == confirmEmail) {
                Log.d("EmailFragment", "Updating email with token: $token")
                token?.let {
                    viewModel.updateEmail(newEmail, it) { success, message ->
                        if (success) {
                            Log.d("EmailFragment", "Email updated successfully: $message")
                            // Handle successful email update (e.g., show a success message)
                        } else {
                            Log.e("EmailFragment", "Failed to update email: $message")
                            // Handle failed email update (e.g., show an error message)
                        }
                    }
                } ?: run {
                    Log.e("EmailFragment", "Token is null")
                    // Handle token fetch failure (e.g., show an error message)
                }
            } else {
                Log.e("EmailFragment", "Email mismatch")
                // Handle email mismatch (e.g., show an error message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TOKEN = "token"

        fun newInstance(token: String) = EmailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TOKEN, token)
            }
        }
    }

    private fun setupView() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true // Optional: Set status bar content to dark
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green)
        }
    }
}