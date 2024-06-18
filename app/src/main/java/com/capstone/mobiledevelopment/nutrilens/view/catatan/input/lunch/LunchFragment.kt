package com.capstone.mobiledevelopment.nutrilens.view.catatan.input.lunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Lunch
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodItem

class LunchFragment : Fragment() {
    private lateinit var foodList: MutableList<FoodItem>
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lunch, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Initialize the food list
        foodList = mutableListOf()

        // Set up the adapter and RecyclerView
        adapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        // Retrieve data from arguments
        arguments?.getParcelable<Lunch>("selected_meal")?.let { lunch ->
            updateFoodList(lunch)
        }
    }

    private fun updateFoodList(lunch: Lunch) {
        foodList.clear()
        val newFoodItem = FoodItem(
            "Lunch",
            lunch.total?.carbs ?: 0.0,
            lunch.total?.fat ?: 0.0,
            lunch.total?.prot ?: 0.0,
            (lunch.total?.calories ?: 0.0).toInt().toDouble(),
            lunch.data?.map {
                FoodItem.FoodDetail(
                    it?.foodName ?: "",
                    it?.carbohydrate ?: 0.0,
                    it?.fat ?: 0.0,
                    it?.proteins ?: 0.0,
                    (it?.calories ?: 0.0).toInt().toDouble()
                )
            }?.toMutableList() ?: mutableListOf()
        )
        foodList.add(newFoodItem)
        adapter.notifyDataSetChanged()
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
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green2)
        }
    }
}