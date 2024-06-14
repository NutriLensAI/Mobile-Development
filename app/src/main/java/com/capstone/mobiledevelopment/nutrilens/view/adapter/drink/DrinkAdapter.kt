package com.capstone.mobiledevelopment.nutrilens.view.adapter.drink

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.drink.AddDrink

class DrinkAdapter(private val drinkList: List<DrinkItem>) :
    RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder>() {

    class DrinkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drinkTitle: TextView = view.findViewById(R.id.drinkTitle)
        val drinkAmount: TextView = view.findViewById(R.id.drinkAmountValue)
        val drinkSugar: TextView = view.findViewById(R.id.drinkSugarValue)
        val drinkListContainer: ViewGroup = view.findViewById(R.id.drinkListContainer)
        val btnAddDrink: ImageButton = view.findViewById(R.id.btnAddDrink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drink, parent, false)
        return DrinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        val drinkItem = drinkList[position]
        holder.drinkTitle.text = drinkItem.drinkTitle
        holder.drinkAmount.text = "${drinkItem.amount} ml"
        holder.drinkSugar.text = "${drinkItem.sugar} g"

        holder.drinkListContainer.removeAllViews()
        for (drinkDetail in drinkItem.drinkDetails) {
            val drinkView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.drink_detail_item, holder.drinkListContainer, false)
            drinkView.findViewById<TextView>(R.id.drinkName).text = drinkDetail.name
            drinkView.findViewById<TextView>(R.id.drinkAmount).text = "${drinkDetail.amount} ml"
            drinkView.findViewById<TextView>(R.id.drinkSugar).text = "${drinkDetail.sugar} g"

            holder.drinkListContainer.addView(drinkView)
        }

        // Set the OnClickListener for the add button
        holder.btnAddDrink.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AddDrink::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = drinkList.size
}