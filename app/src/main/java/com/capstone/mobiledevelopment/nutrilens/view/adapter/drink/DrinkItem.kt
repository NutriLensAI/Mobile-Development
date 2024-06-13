package com.capstone.mobiledevelopment.nutrilens.view.adapter.drink

data class DrinkItem(
    val drinkTitle: String,
    val amount: Int,
    val sugar: Int,
    val drinkDetails: MutableList<DrinkDetail> = mutableListOf()
) {
    data class DrinkDetail(val name: String, val amount: Int, val sugar: Int)
}