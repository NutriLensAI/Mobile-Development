package com.capstone.mobiledevelopment.nutrilens.view.settings.email

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserPreference
import com.capstone.mobiledevelopment.nutrilens.data.pref.dataStore
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.ApiConfig
import com.capstone.mobiledevelopment.nutrilens.databinding.FragmentEmailBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: EmailViewModel
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveTokenAndSetupRepository()
    }

    private fun setupListeners() {
        binding.btnUpdateEmail.setOnClickListener {
            val newEmail = binding.etNewEmail.text.toString()
            val confirmEmail = binding.etConfirmNewEmail.text.toString()

            if (newEmail == confirmEmail) {
                viewModel.updateEmail(token, newEmail) { success ->
                    if (success) {
                        // Handle successful email update (e.g., show a success message)
                    } else {
                        // Handle failed email update (e.g., show an error message)
                    }
                }
            } else {
                // Handle email mismatch (e.g., show an error message)
            }
        }
    }

    private fun retrieveTokenAndSetupRepository() {
        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(requireContext().dataStore)
            val userSession = userPreference.getSession().first()
            token = userSession.token
            Log.d("EmailFragment", "Token: $token")  // Log the token to ensure it's correct

            if (token.isEmpty()) {
                Log.e("EmailFragment", "Token is empty, using fallback token")
                // Use a fallback token for testing
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTMsImVtYWlsIjoidGVzdGluZ2FqYUBnbWFpbC5jb20iLCJpYXQiOjE3MTgwNzQ1MTUsImV4cCI6MTcxODA3ODExNX0.WQ43GZJMoevVHh7wz8P9yUm1i2hyWmWPSKdFrFd4tf8"
            }

            userRepository = UserRepository.getInstance(
                userPreference,
                ApiConfig.getApiService(token)
            )

            // Initialize the ViewModel now that the repository is ready
            viewModel = ViewModelProvider(this@EmailFragment, EmailViewModelFactory(userRepository))[EmailViewModel::class.java]
            setupListeners()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = EmailFragment()
    }
}
