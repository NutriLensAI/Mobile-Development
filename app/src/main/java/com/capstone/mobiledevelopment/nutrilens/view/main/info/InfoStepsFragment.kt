package com.capstone.mobiledevelopment.nutrilens.view.main.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.capstone.mobiledevelopment.nutrilens.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoStepsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoStepsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_steps, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            InfoStepsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
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