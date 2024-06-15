package com.capstone.mobiledevelopment.nutrilens.view.adapter.resep

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.resep.DetailActivity
import com.capstone.mobiledevelopment.nutrilens.view.resep.ResepItem

class ResepAdapter(
    private var resepList: MutableList<ResepItem>
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

        // Set background color based on position
        val color = Color.parseColor(colors[position % colors.size])
        holder.itemView.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("EXTRA_TITLE", resep.Title)
            intent.putExtra("EXTRA_INGREDIENTS", resep.Ingredients)
            intent.putExtra("EXTRA_STEPS", resep.Steps)
            context.startActivity(intent)
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

    class ResepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
    }
}
