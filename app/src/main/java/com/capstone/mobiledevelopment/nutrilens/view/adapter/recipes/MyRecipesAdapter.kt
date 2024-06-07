package com.capstone.mobiledevelopment.nutrilens.view.adapter.recipes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.resep.Detail
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe

class MyRecipesAdapter(
    private var myRecipesList: List<FavoriteRecipe>,
    private val context: Context
) : RecyclerView.Adapter<MyRecipesAdapter.MyRecipesViewHolder>() {

    class MyRecipesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tv_item_name)
        val tvItemIngredients: TextView = view.findViewById(R.id.tv_item_ingredients)
        val tvItemSteps: TextView = view.findViewById(R.id.tv_item_steps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return MyRecipesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecipesViewHolder, position: Int) {
        val recipe = myRecipesList[position]
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

    override fun getItemCount(): Int = myRecipesList.size

    fun updateList(newList: List<FavoriteRecipe>) {
        myRecipesList = newList
        notifyDataSetChanged()
    }
}
