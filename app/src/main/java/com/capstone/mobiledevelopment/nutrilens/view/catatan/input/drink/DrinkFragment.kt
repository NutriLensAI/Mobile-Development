package com.capstone.mobiledevelopment.nutrilens.view.catatan.input.drink

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
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

        return view
    }

    override fun onResume() {
        super.onResume()
        // Fetch drink data
        fetchDrinkAndSugarData()
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

        if (drinks.isEmpty()) {
            val newDrinkItem = DrinkItem(
                drinkTitle = "Drink",
                amount = 0,
                sugar = 0,
                drinkDetails = mutableListOf()
            )
            drinkList.add(newDrinkItem)
        } else {
            val newDrinkItem = DrinkItem(
                drinkTitle = "Drink",
                amount = drinks.sumOf { it.amount },
                sugar = drinks.sumOf { it.sugar },
                drinkDetails = drinks.map {
                    DrinkItem.DrinkDetail(it.name, it.amount, it.sugar)
                }.toMutableList()
            )
            drinkList.add(newDrinkItem)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true // Set status bar content to dark
                controller.isAppearanceLightNavigationBars =
                    true // Set navigation bar content to dark
            }
            activity?.actionBar?.hide()
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green)
            window.navigationBarColor = ContextCompat.getColor(
                requireContext(),
                R.color.white
            ) // Change navigation bar color
        }
    }
}