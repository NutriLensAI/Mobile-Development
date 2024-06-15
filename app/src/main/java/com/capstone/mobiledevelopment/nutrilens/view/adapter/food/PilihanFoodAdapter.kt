package com.capstone.mobiledevelopment.nutrilens.view.adapter.food

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.retrofit.RecommendedFood

class PilihanFoodAdapter(
    private var foodList: List<RecommendedFood>,
    private val onAddFoodClicked: (RecommendedFood) -> Unit
) : RecyclerView.Adapter<PilihanFoodAdapter.FoodViewHolder>() {

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
        holder.foodName.text = if (food.isRecommended) {
            "${food.name} (Rekomendasi)"
        } else {
            food.name
        }

        holder.foodCalories.text = "${food.calories} Cal"

        if (food.isRecommended) {
            // Menggunakan gambar dari drawable untuk makanan rekomendasi
            Glide.with(holder.itemView.context).load(R.drawable.image_9).into(holder.foodImage)
        } else {
            // Menggunakan URL gambar dari API untuk makanan non-rekomendasi
            food.image?.let {
                Glide.with(holder.itemView.context).load(it).into(holder.foodImage)
            }
        }

        holder.addFoodIcon.setOnClickListener {
            onAddFoodClicked(food)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateList(newList: List<RecommendedFood>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
