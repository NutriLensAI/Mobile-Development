package com.capstone.mobiledevelopment.nutrilens.view.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
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
        ViewModelFactory.getInstance(requireActivity())
    }

    private var navigateTo: String? = null
    private var isGuestUser: Boolean = false

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                startCrop(it)
            }
        }

    private val cropImage = registerForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                saveCroppedImageToPreferences(uri)
                binding.profileImage.setImageURI(uri)
            }
        } else {
            val exception = result.error
            Log.e(TAG, "Crop failed: ${exception?.message}", exception)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navigateTo = it.getString(ARG_NAVIGATE_TO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchEmail()
        viewModel.fetchToken()
        viewModel.fetchUsername()

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(activity, WelcomeActivity::class.java))
                activity?.finish()
            } else {
                isGuestUser = user.isGuest
            }
        }

        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                binding.emailSetting.setOnClickListener {
                    if (isGuestUser) {
                        showLoginDialog()
                    } else {
                        navigateToFragment(EmailFragment.newInstance(token))
                    }
                }

                binding.passwordSetting.setOnClickListener {
                    if (isGuestUser) {
                        showLoginDialog()
                    } else {
                        navigateToFragment(PasswordFragment.newInstance(token))
                    }
                }

                binding.personalInfoSetting.setOnClickListener {
                    if (isGuestUser) {
                        showLoginDialog()
                    } else {
                        navigateToFragment(PersonalFragment.newInstance(token))
                    }
                }

                if (navigateTo == "PersonalFragment") {
                    navigateToFragment(PersonalFragment.newInstance(token))
                }
            }
        }

        viewModel.username.observe(viewLifecycleOwner) { username ->
            binding.profileName.text = "Halo, $username"
        }

        binding.languageSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.profileImage.setOnClickListener {
            openGallery()
        }

        setupView()
        setupAction()
        loadImageFromPreferences()
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)  // Optional: Adds the transaction to the backstack
            .commit()
    }

    private fun showLoginDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Kamu harus login untuk menggunakan fitur ini")
        builder.setMessage("Silakan login untuk melanjutkan atau pilih Later untuk menggunakan akun guest.")
        builder.setPositiveButton("Login Now") { dialog, _ ->
            val intent = Intent(activity, WelcomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        builder.setNegativeButton("Later") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView).let { controller ->
                controller.isAppearanceLightStatusBars =
                    true // Optional: Set status bar content to dark
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green2)
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

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun startCrop(uri: Uri) {
        val cropImageContractOptions = CropImageContractOptions(
            uri,
            CropImageOptions()
        )
        cropImage.launch(cropImageContractOptions)
    }

    private fun saveCroppedImageToPreferences(uri: Uri) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("profileImageUri", uri.toString())
        editor.apply()
    }

    private fun loadImageFromPreferences() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val imageUriString = sharedPreferences.getString("profileImageUri", null)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.profileImage.setImageURI(imageUri)
        }
    }

    companion object {
        private const val TAG = "SettingsFragment"
        private const val ARG_NAVIGATE_TO = "navigate_to"

        fun newInstance(selectedItemId: Int, navigateTo: String?) = SettingsFragment().apply {
            arguments = Bundle().apply {
                putInt("selected_item", selectedItemId)
                putString(ARG_NAVIGATE_TO, navigateTo)
            }
        }
    }

}
