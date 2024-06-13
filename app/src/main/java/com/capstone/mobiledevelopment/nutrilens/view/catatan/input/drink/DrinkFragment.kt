package com.capstone.mobiledevelopment.nutrilens.view.catatan.input.drink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.Drink
import com.capstone.mobiledevelopment.nutrilens.data.database.drink.DrinkDatabase
import com.capstone.mobiledevelopment.nutrilens.view.adapter.drink.DrinkAdapter
import com.capstone.mobiledevelopment.nutrilens.view.adapter.drink.DrinkItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrinkFragment : Fragment() {
    private lateinit var drinkList: MutableList<DrinkItem>
    private lateinit var adapter: DrinkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drink, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Initialize the drink list
        drinkList = mutableListOf()

        // Set up the adapter and RecyclerView
        adapter = DrinkAdapter(drinkList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Fetch drink data
        fetchDrinkAndSugarData()

        return view
    }

    private fun fetchDrinkAndSugarData() {
        val drinkDao = DrinkDatabase.getDatabase(requireContext()).drinkDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val drinks = drinkDao.getAllDrinks()
            withContext(Dispatchers.Main) {
                updateDrinkList(drinks)
            }
        }
    }

    private fun updateDrinkList(drinks: List<Drink>) {
        drinkList.clear()
        for (drink in drinks) {
            val newDrinkItem = DrinkItem(
                drink.name,
                drink.amount,
                drink.sugar,
                mutableListOf(DrinkItem.DrinkDetail(drink.name, drink.amount, drink.sugar))
            )
            drinkList.add(newDrinkItem)
        }
        adapter.notifyDataSetChanged()
    }
}