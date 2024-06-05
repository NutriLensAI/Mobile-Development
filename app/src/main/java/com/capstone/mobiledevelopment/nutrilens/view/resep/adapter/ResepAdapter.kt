package com.capstone.mobiledevelopment.nutrilens.view.resep.adapter

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

class ResepAdapter(private val resepList: List<ResepItem>) : RecyclerView.Adapter<ResepAdapter.ResepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resep_card, parent, false)
        return ResepViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resep = resepList[position]
        holder.tvItemName.text = resep.name
        holder.tvItemDescription.text = resep.description
        // Set image resource from drawable
        holder.ivItemPhoto.setImageResource(getImageResource(holder.itemView.context, resep.imageUrl))

        // Set click listener to send data to Detail activity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, Detail::class.java).apply {
                putExtra("EXTRA_IMAGE", resep.imageUrl)
                putExtra("EXTRA_TITLE", resep.name)
                putExtra("EXTRA_DESCRIPTION", resep.description)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = resepList.size

    private fun getImageResource(context: Context, imageName: String): Int {
        return context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }

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
