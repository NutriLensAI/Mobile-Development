package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R

class MyRecipesAdapter(private var recipeList: List<MyRecipe>) : RecyclerView.Adapter<MyRecipesAdapter.MyRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return MyRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.tvItemName.text = recipe.title
        holder.tvItemIngredients.text = recipe.ingredients.replace("--", "\n")
        holder.tvItemSteps.text = recipe.steps.replace("--", "\n")
    }

    override fun getItemCount(): Int = recipeList.size

    fun updateList(newList: List<MyRecipe>) {
        recipeList = newList
        notifyDataSetChanged()
    }

    class MyRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvItemIngredients: TextView = itemView.findViewById(R.id.tv_item_ingredients)
        val tvItemSteps: TextView = itemView.findViewById(R.id.tv_item_steps)
    }
}
