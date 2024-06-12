package com.capstone.mobiledevelopment.nutrilens.view.adapter.food

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.resep.Detail
import com.capstone.mobiledevelopment.nutrilens.data.database.favorite.FavoriteRecipe

class FavoriteRecipeAdapter(
    private var favoriteRecipes: List<FavoriteRecipe>,
    private val context: Context
) : RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteRecipeViewHolder>() {

    class FavoriteRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvItemIngredients: TextView = itemView.findViewById(R.id.tv_item_ingredients)
        val tvItemSteps: TextView = itemView.findViewById(R.id.tv_item_steps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return FavoriteRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteRecipeViewHolder, position: Int) {
        val recipe = favoriteRecipes[position]
        holder.tvItemName.text = recipe.title
        holder.tvItemIngredients.text = recipe.ingredients.replace("--", "\n")
        holder.tvItemSteps.text = recipe.steps.replace("--", "\n")

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Detail::class.java).apply {
                putExtra("EXTRA_TITLE", recipe.title)
                putExtra("EXTRA_INGREDIENTS", recipe.ingredients)
                putExtra("EXTRA_STEPS", recipe.steps)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = favoriteRecipes.size

    fun updateList(newList: List<FavoriteRecipe>) {
        favoriteRecipes = newList
        notifyDataSetChanged()
    }
}
