package com.capstone.mobiledevelopment.nutrilens.view.adapter.food

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.pilihan.PilihanMakananActivity

data class FoodItem(
    val mealTitle: String,
    var carbs: Double,
    var fat: Double,
    var protein: Double,
    var calories: Double,
    val foodItems: MutableList<FoodDetail> = mutableListOf()
) {
    data class FoodDetail(
        val nama: String,
        val carbs: Double,
        val fat: Double,
        val protein: Double,
        val calories: Double
    )
}

class FoodAdapter(private val foodList: List<FoodItem>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealTitle: TextView = view.findViewById(R.id.mealTitle)
        val carbsValue: TextView = view.findViewById(R.id.carbsValue)
        val fatValue: TextView = view.findViewById(R.id.fatValue)
        val proteinValue: TextView = view.findViewById(R.id.proteinValue)
        val caloriesValue: TextView = view.findViewById(R.id.caloriesValue)
        val foodListContainer: ViewGroup = view.findViewById(R.id.foodListContainer)
        val btnAddFood: ImageButton = view.findViewById(R.id.btnAddFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catatan, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.mealTitle.text = foodItem.mealTitle
        holder.carbsValue.text = "${foodItem.carbs} g"
        holder.fatValue.text = "${foodItem.fat} g"
        holder.proteinValue.text = "${foodItem.protein} g"
        holder.caloriesValue.text = "${foodItem.calories}"

        holder.foodListContainer.removeAllViews()
        for (foodDetail in foodItem.foodItems) {
            val foodView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.food_detail_item, holder.foodListContainer, false)
            foodView.findViewById<TextView>(R.id.foodName).text = foodDetail.nama
            foodView.findViewById<TextView>(R.id.foodCarbs).text = "${foodDetail.carbs} g"
            foodView.findViewById<TextView>(R.id.foodFat).text = "${foodDetail.fat} g"
            foodView.findViewById<TextView>(R.id.foodProtein).text = "${foodDetail.protein} g"
            foodView.findViewById<TextView>(R.id.foodCalories).text = "${foodDetail.calories}"

            holder.foodListContainer.addView(foodView)
        }

        // Set the OnClickListener for the add button
        holder.btnAddFood.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PilihanMakananActivity::class.java)
            intent.putExtra("meal_type", foodItem.mealTitle)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = foodList.size
}
