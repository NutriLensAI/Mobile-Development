package com.capstone.mobiledevelopment.nutrilens.resep.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R

class ResepAdapter(private val resepList: List<ResepItem>) : RecyclerView.Adapter<ResepAdapter.ResepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return ResepViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resep = resepList[position]
        holder.tvItemName.text = resep.name
        holder.tvItemDescription.text = resep.description
        // Set image resource or use image loading library for ivItemPhoto
    }

    override fun getItemCount(): Int = resepList.size

    class ResepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivItemPhoto: ImageView = itemView.findViewById(R.id.iv_item_photo)
        val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvItemDescription: TextView = itemView.findViewById(R.id.tvItemDescript)
    }
}

data class ResepItem(
    val name: String,
    val description: String,
    val imageUrl: String
)
