package com.capstone.mobiledevelopment.nutrilens.view.utils

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val error: Throwable) : Result<Nothing>()
}