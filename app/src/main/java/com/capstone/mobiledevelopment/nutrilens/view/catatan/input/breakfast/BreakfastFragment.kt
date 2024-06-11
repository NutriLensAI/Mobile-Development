package com.capstone.mobiledevelopment.nutrilens.view.catatan.input.breakfast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Breakfast
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodItem

class BreakfastFragment : Fragment() {
    private lateinit var foodList: MutableList<FoodItem>
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_breakfast, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Initialize the food list
        foodList = mutableListOf()

        // Set up the adapter and RecyclerView
        adapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Retrieve data from arguments
        arguments?.getParcelable<Breakfast>("selected_meal")?.let { breakfast ->
            updateFoodList(breakfast)
        }

        return view
    }

    private fun updateFoodList(breakfast: Breakfast) {
        foodList.clear()
        val newFoodItem = FoodItem(
            "Breakfast",
            breakfast.total?.carbs ?: 0,
            breakfast.total?.fat ?: 0,
            breakfast.total?.prot ?: 0,
            breakfast.total?.calories ?: 0,
            breakfast.data?.map {
                FoodItem.FoodDetail(
                    it?.foodName ?: "",
                    it?.carbohydrate ?: 0,
                    it?.fat ?: 0,
                    it?.proteins ?: 0,
                    it?.calories ?: 0
                )
            }?.toMutableList() ?: mutableListOf()
        )
        foodList.add(newFoodItem)
        adapter.notifyDataSetChanged()
    }
}