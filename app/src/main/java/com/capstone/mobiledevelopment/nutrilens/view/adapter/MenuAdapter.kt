package com.capstone.mobiledevelopment.nutrilens.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.databinding.ItemMenuBinding
import com.capstone.mobiledevelopment.nutrilens.view.main.InfoCholesterol
import com.capstone.mobiledevelopment.nutrilens.view.main.InfoDrink
import com.capstone.mobiledevelopment.nutrilens.view.main.InfoSteps
import com.capstone.mobiledevelopment.nutrilens.view.main.InfoSugar

data class MenuItem(
    val title: String,
    val imageResId: Int,
    val value: String,
    val info: String
)

class MenuAdapter(private val menuList: MutableList<MenuItem>) :
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

        holder.binding.root.findViewById<ImageView>(R.id.menuInfoImageView).setOnClickListener {
            val context = holder.itemView.context
            when (menuItem.title) {
                "Sugar" -> {
                    val intent = Intent(context, InfoSugar::class.java)
                    context.startActivity(intent)
                }
                "Cholesterol" -> {
                    val intent = Intent(context, InfoCholesterol::class.java)
                    context.startActivity(intent)
                }
                "Steps" -> {
                    val intent = Intent(context, InfoSteps::class.java)
                    context.startActivity(intent)
                }
                "Drink" -> {
                    val intent = Intent(context, InfoDrink::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = menuList.size

    fun updateDrinkAmount(amount: String) {
        val index = menuList.indexOfFirst { it.title == "Drink" }
        if (index != -1) {
            menuList[index] = menuList[index].copy(value = amount)
            notifyItemChanged(index)
        }
    }
}
