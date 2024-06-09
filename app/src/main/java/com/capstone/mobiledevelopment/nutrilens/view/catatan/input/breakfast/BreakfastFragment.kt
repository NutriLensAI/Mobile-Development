package com.capstone.mobiledevelopment.nutrilens.view.catatan.input.breakfast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
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
        foodList = mutableListOf(
            FoodItem("Breakfast", 14, 14, 14, 500, mutableListOf(FoodItem.FoodDetail("Nasi Gudeg Rawon", 14, 14, 14, 500)))
        )

        // Set up the adapter and RecyclerView
        adapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Retrieve data from arguments or savedInstanceState
        arguments?.let {
            val mealType = it.getString("meal_type")
            val namaMakanan = it.getString("nama_makanan")
            val calories = it.getInt("calories", 0)
            val carbs = it.getInt("carbs", 0)
            val fat = it.getInt("fat", 0)
            val protein = it.getInt("protein", 0)

            if (mealType != null && namaMakanan != null) {
                addFoodToMeal(mealType, namaMakanan, calories, carbs, fat, protein)
            }
        }

        return view
    }

    private fun addFoodToMeal(mealType: String, namaMakanan: String, calories: Int, carbs: Int, fat: Int, protein: Int) {
        val existingFoodItem = foodList.find { it.mealTitle.equals(mealType, ignoreCase = true) }
        if (existingFoodItem != null) {
            val index = foodList.indexOf(existingFoodItem)
            existingFoodItem.foodItems.add(FoodItem.FoodDetail(namaMakanan, carbs, fat, protein, calories))
            existingFoodItem.carbs += carbs
            existingFoodItem.fat += fat
            existingFoodItem.protein += protein
            existingFoodItem.calories += calories
            adapter.notifyItemChanged(index)
        } else {
            val newFoodItem = FoodItem(mealType, carbs, fat, protein, calories, mutableListOf(
                FoodItem.FoodDetail(namaMakanan, carbs, fat, protein, calories)))
            foodList.add(newFoodItem)
            adapter.notifyItemInserted(foodList.size - 1)
        }
    }
}