package com.capstone.mobiledevelopment.nutrilens.view.adapter.macros

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R

class MakananAdapter(private val makananList: List<Makanan>) : RecyclerView.Adapter<MakananAdapter.MakananViewHolder>() {

    class MakananViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMakanan: ImageView = view.findViewById(R.id.img_makanan)
        val tvNamaMakanan: TextView = view.findViewById(R.id.tv_nama_makanan)
        val tvKcal: TextView = view.findViewById(R.id.tv_kcal)
        val tvMacros: TextView = view.findViewById(R.id.tv_macros)
        val tvMacrosPercentage: TextView = view.findViewById(R.id.tv_macros_percentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_makanan, parent, false)
        return MakananViewHolder(view)
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        val makanan = makananList[position]
        holder.tvNamaMakanan.text = makanan.nama
        holder.tvKcal.text = "${makanan.calories} Kcal"
        holder.tvMacros.text = "Carbs: ${makanan.carbs}g, Fat: ${makanan.fat}g, Protein: ${makanan.protein}g"
        holder.tvMacrosPercentage.text = "Carbs: ${makanan.carbsPercentage}, Fat: ${makanan.fatPercentage}, Protein: ${makanan.proteinPercentage}"
        // Load image from camera or other source into holder.imgMakanan
    }

    override fun getItemCount(): Int {
        return makananList.size
    }
}
