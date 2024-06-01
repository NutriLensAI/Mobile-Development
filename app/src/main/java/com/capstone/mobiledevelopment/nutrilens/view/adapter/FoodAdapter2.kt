package com.capstone.mobiledevelopment.nutrilens.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R

class FoodAdapter2(private val foodList: List<Food>, private val onAddClickListener: (Food) -> Unit) : RecyclerView.Adapter<FoodAdapter2.FoodViewHolder>() {

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFoodName: TextView = view.findViewById(R.id.tv_food_name)
        val tvFoodCalories: TextView = view.findViewById(R.id.tv_food_calories)
        val ivAddFood: ImageView = view.findViewById(R.id.iv_add_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.tvFoodName.text = foodItem.name
        holder.tvFoodCalories.text = "${foodItem.calories} Cal"
        holder.ivAddFood.setOnClickListener {
            onAddClickListener(foodItem)
        }
    }

    override fun getItemCount() = foodList.size
}
