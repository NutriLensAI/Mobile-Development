package com.capstone.mobiledevelopment.nutrilens.view.adapter.resep

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.resep.Detail
import com.capstone.mobiledevelopment.nutrilens.view.resep.ResepItem

class ResepAdapter(
    private var resepList: MutableList<ResepItem>,
    private val onFavoriteClickListener: (ResepItem) -> Unit
) : RecyclerView.Adapter<ResepAdapter.ResepViewHolder>() {

    private val colors = listOf(
        "#AEC6CF", // Blue pastel
        "#FFB3B3", // Red pastel
        "#FFFACD", // Yellow pastel
        "#B2E8A4"  // Green pastel
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return ResepViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resep = resepList[position]
        holder.tvItemName.text = resep.Title
        holder.tvItemIngredients.text = resep.Ingredients.replace("--", "\n")
        holder.tvItemSteps.text = resep.Steps.replace("--", "\n")

        // Set background color based on position
        val color = Color.parseColor(colors[position % colors.size])
        holder.itemView.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, Detail::class.java)
            intent.putExtra("EXTRA_TITLE", resep.Title)
            intent.putExtra("EXTRA_INGREDIENTS", resep.Ingredients)
            intent.putExtra("EXTRA_STEPS", resep.Steps)
            context.startActivity(intent)
        }

        holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(holder.favoriteButton.context, R.drawable.favorite_button_selector))
        holder.favoriteButton.isSelected = false

        holder.favoriteButton.setOnClickListener {
            holder.favoriteButton.isSelected = !holder.favoriteButton.isSelected
            animateFavoriteButton(holder.favoriteButton)
            onFavoriteClickListener(resep)
        }
    }

    override fun getItemCount(): Int = resepList.size

    fun addRecipes(newRecipes: List<ResepItem>) {
        val startPos = resepList.size
        resepList.addAll(newRecipes)
        notifyItemRangeInserted(startPos, newRecipes.size)
    }

    fun updateRecipes(newRecipes: List<ResepItem>) {
        resepList.clear()
        resepList.addAll(newRecipes)
        notifyDataSetChanged()
    }

    private fun animateFavoriteButton(button: ImageView) {
        val scaleAnimation = ScaleAnimation(
            0.7f, 1.2f, 0.7f, 1.2f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 200
        scaleAnimation.fillAfter = true
        button.startAnimation(scaleAnimation)
    }

    class ResepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvItemIngredients: TextView = itemView.findViewById(R.id.tv_item_ingredients)
        val tvItemSteps: TextView = itemView.findViewById(R.id.tv_item_steps)
        val favoriteButton: ImageView = itemView.findViewById(R.id.iv_favorite)
    }
}
