package com.capstone.mobiledevelopment.nutrilens.view.adapter.food

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.resep.Detail
import com.capstone.mobiledevelopment.nutrilens.view.resep.favorite.FavoriteRecipe

class FavoriteRecipeAdapter(
    private var favoriteList: List<FavoriteRecipe>,
    private val context: Context
) : RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tv_item_name)
        val tvItemIngredients: TextView = view.findViewById(R.id.tv_item_ingredients)
        val tvItemSteps: TextView = view.findViewById(R.id.tv_item_steps)
        val favoriteButton: ImageView = view.findViewById(R.id.iv_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]
        holder.tvItemName.text = favorite.title
        holder.tvItemIngredients.text = favorite.ingredients.replace("--", "\n")
        holder.tvItemSteps.text = favorite.steps.replace("--", "\n")

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Detail::class.java).apply {
                putExtra("EXTRA_TITLE", favorite.title)
                putExtra("EXTRA_INGREDIENTS", favorite.ingredients)
                putExtra("EXTRA_STEPS", favorite.steps)
            }
            context.startActivity(intent)
        }

        holder.favoriteButton.setOnClickListener {
            // Handle removing favorite if necessary
        }

        holder.favoriteButton.setImageResource(R.drawable.ic_heart_filled)
    }

    override fun getItemCount(): Int = favoriteList.size

    fun updateList(newList: List<FavoriteRecipe>) {
        favoriteList = newList
        notifyDataSetChanged()
    }
}
