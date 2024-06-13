package com.capstone.mobiledevelopment.nutrilens.view.catatan.input.dinner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.reponse.Dinner
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.food.FoodItem

class DinnerFragment : Fragment() {
    private lateinit var foodList: MutableList<FoodItem>
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dinner, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Initialize the food list
        foodList = mutableListOf()

        // Set up the adapter and RecyclerView
        adapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Retrieve data from arguments
        arguments?.getParcelable<Dinner>("selected_meal")?.let { dinner ->
            updateFoodList(dinner)
        }

        return view
    }

    private fun updateFoodList(dinner: Dinner) {
        foodList.clear()
        val newFoodItem = FoodItem(
            "Dinner",
            dinner.total?.carbs ?: 0.0,
            dinner.total?.fat ?: 0.0,
            dinner.total?.prot ?: 0.0,
            dinner.total?.calories ?: 0.0,
            dinner.data?.map {
                FoodItem.FoodDetail(
                    it?.foodName ?: "",
                    it?.carbohydrate ?: 0.0,
                    it?.fat ?: 0.0,
                    it?.proteins ?: 0.0,
                    it?.calories ?: 0.0
                )
            }?.toMutableList() ?: mutableListOf()
        )
        foodList.add(newFoodItem)
        adapter.notifyDataSetChanged()
    }
}