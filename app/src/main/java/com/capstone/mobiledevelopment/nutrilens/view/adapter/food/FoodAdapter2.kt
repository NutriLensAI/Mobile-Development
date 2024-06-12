package com.capstone.mobiledevelopment.nutrilens.view.adapter.food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.mobiledevelopment.nutrilens.R

class FoodAdapter2(private var foodList: List<FoodResponse>) :
    RecyclerView.Adapter<FoodAdapter2.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.tv_food_name)
        val foodCalories: TextView = itemView.findViewById(R.id.tv_food_calories)
        val foodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        val addFoodIcon: ImageView = itemView.findViewById(R.id.iv_add_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.foodName.text = food.name
        holder.foodCalories.text = "${food.calories} Cal"
        Glide.with(holder.itemView.context).load(food.image).into(holder.foodImage)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateList(newList: List<FoodResponse>) {
        foodList = newList
        notifyDataSetChanged()
    }
}




