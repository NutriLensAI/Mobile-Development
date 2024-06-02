package com.capstone.mobiledevelopment.nutrilens.view.settings.personal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.capstone.mobiledevelopment.nutrilens.R

/**
 * A simple [Fragment] subclass.
 * Use the [InputDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputDataFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_input_data, container, false)

        // Setup the button click listener
        view.findViewById<Button>(R.id.buttonSave).setOnClickListener {
            // Collect data from your inputs
            val weight = view.findViewById<EditText>(R.id.tiWeight).text.toString()
            val height = view.findViewById<EditText>(R.id.tiHeight).text.toString()
            val age = view.findViewById<EditText>(R.id.tiAge).text.toString()
            val gender = view.findViewById<Spinner>(R.id.spinnerGender).selectedItem.toString()

            // Pass data to PersonalFragment
            val personalFragment = PersonalFragment.newInstance(weight, height, age, gender)
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, personalFragment)
                ?.commit()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment InputDataFragment.
         */
        @JvmStatic
        fun newInstance() = InputDataFragment()
    }
}