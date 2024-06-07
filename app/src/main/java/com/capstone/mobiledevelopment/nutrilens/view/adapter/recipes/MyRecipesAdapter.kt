package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.databinding.ItemMyRecipeBinding

class MyRecipesAdapter(
    private var recipes: List<MyRecipe>,
    private val onDeleteClick: (MyRecipe) -> Unit
) : RecyclerView.Adapter<MyRecipesAdapter.MyRecipesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipesViewHolder {
        val binding = ItemMyRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyRecipesViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateList(newRecipes: List<MyRecipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    inner class MyRecipesViewHolder(private val binding: ItemMyRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: MyRecipe) {
            binding.tvItemName.text = recipe.title
            binding.tvItemIngredients.text = recipe.ingredients
            binding.tvItemSteps.text = recipe.steps
            binding.btnDelete.setOnClickListener {
                onDeleteClick(recipe)
            }
        }
    }
}