package com.capstone.mobiledevelopment.nutrilens.view.adapter.info

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.databinding.ItemMenuBinding
import com.capstone.mobiledevelopment.nutrilens.view.main.info.InfoActivity

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

        val context = holder.itemView.context

        // Set OnClickListener for the CardView to navigate to the appropriate info page
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, InfoActivity::class.java).apply {
                putExtra("FRAGMENT_TYPE", menuItem.title)
            }
            context.startActivity(intent)
        }

        // Set OnClickListener for the info icon to navigate to the appropriate info page
        holder.binding.root.findViewById<ImageView>(com.capstone.mobiledevelopment.nutrilens.R.id.menuInfoImageView).setOnClickListener {
            val intent = Intent(context, InfoActivity::class.java).apply {
                putExtra("FRAGMENT_TYPE", menuItem.title)
            }
            context.startActivity(intent)
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
