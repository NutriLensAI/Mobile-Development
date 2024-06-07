package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe

class MyRecipesAdapter(
    private var recipes: List<FavoriteRecipe>
) : RecyclerView.Adapter<MyRecipesAdapter.MyRecipesViewHolder>() {

    class MyRecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvItemIngredients: TextView = itemView.findViewById(R.id.tv_item_ingredients)
        val tvItemSteps: TextView = itemView.findViewById(R.id.tv_item_steps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return MyRecipesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecipesViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.tvItemName.text = recipe.title
        holder.tvItemIngredients.text = recipe.ingredients.replace("--", "\n")
        holder.tvItemSteps.text = recipe.steps.replace("--", "\n")
    }

    override fun getItemCount(): Int = recipes.size

    fun updateList(newList: List<FavoriteRecipe>) {
        recipes = newList
        notifyDataSetChanged()
    }
}
