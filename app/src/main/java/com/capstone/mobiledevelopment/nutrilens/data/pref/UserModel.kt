package com.capstone.mobiledevelopment.nutrilens.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val username: String,
    val isGuest: Boolean = false // Default to false if not provided
)
