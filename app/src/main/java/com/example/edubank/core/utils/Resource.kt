package com.example.edubank.core.utils

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: kotlin.Exception? = null) : Resource<Nothing>()
}