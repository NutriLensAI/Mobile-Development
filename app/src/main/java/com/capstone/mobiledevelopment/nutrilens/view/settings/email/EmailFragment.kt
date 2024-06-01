package com.capstone.mobiledevelopment.nutrilens.view.settings.email

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.mobiledevelopment.nutrilens.R

class EmailFragment : Fragment() {

    companion object {
        fun newInstance() = EmailFragment()
    }

    private val viewModel: EmailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_email, container, false)
    }
}