package com.capstone.mobiledevelopment.nutrilens.view.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.FragmentSettingsBinding
import com.capstone.mobiledevelopment.nutrilens.view.settings.email.EmailFragment
import com.capstone.mobiledevelopment.nutrilens.view.settings.password.PasswordFragment
import com.capstone.mobiledevelopment.nutrilens.view.settings.personal.PersonalFragment
import com.capstone.mobiledevelopment.nutrilens.view.utils.ViewModelFactory
import com.capstone.mobiledevelopment.nutrilens.view.welcome.WelcomeActivity

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(requireActivity())  // Ensure it uses Activity context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileName.text = viewModel.userEmail.value

        viewModel.fetchEmail()
        viewModel.fetchToken()

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(activity, WelcomeActivity::class.java))
                activity?.finish()
            }
        }

        // Observe token and set up navigation for Email and Password Settings
        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                binding.emailSetting.setOnClickListener {
                    navigateToFragment(EmailFragment.newInstance(token))
                }

                binding.passwordSetting.setOnClickListener {
                    navigateToFragment(PasswordFragment.newInstance(token))
                }
                // Setup navigation for Personal Info Settings
                binding.personalInfoSetting.setOnClickListener {
                    navigateToFragment(PersonalFragment.newInstance(token))
                }
            }
        }

        binding.languageSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.backButton.setOnClickListener {
            activity?.finish()
        }

        observeEmail()
        setupView()
        setupAction()
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)  // Optional: Adds the transaction to the backstack
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    private fun observeEmail() {
        viewModel.userEmail.observe(viewLifecycleOwner) { userEmail ->
            val greetingMessage = getString(R.string.greeting, userEmail)
            binding.profileName.text = greetingMessage
        }
    }

    private fun setupAction() {
        binding.actionLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.logout))
            setMessage(getString(R.string.logout_message))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                navigateToWelcomeActivity()
                viewModel.logout()
            }
            create()
            show()
        }
    }

    private fun navigateToWelcomeActivity() {
        startActivity(Intent(activity, WelcomeActivity::class.java))
        activity?.finish()
    }

    companion object {
        // Simplified newInstance method without parameters
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}