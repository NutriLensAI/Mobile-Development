package com.capstone.mobiledevelopment.nutrilens.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.databinding.ItemMenuBinding

data class MenuItem(
    val title: String,
    val imageResId: Int,
    val value: String,
    val info: String
)

class MenuAdapter(private val menuList: List<MenuItem>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]
        holder.binding.menuTitleTextView.text = menuItem.title
        holder.binding.menuImageView.setImageResource(menuItem.imageResId)
        holder.binding.menuValueTextView.text = menuItem.value
        holder.binding.menuInfoTextView.text = menuItem.info
    }

    override fun getItemCount(): Int = menuList.size
}