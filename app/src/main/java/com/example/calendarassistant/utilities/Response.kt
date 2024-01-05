package com.example.calendarassistant.utilities

// TODO: delete, if this class isn't needed - possibly used during networking

sealed class Response<out T : Any> {
    data class Success<out T : Any>(val data: T) : Response<T>()
    data class Error(val exception: Exception) : Response<Nothing>()
    object Loading : Response<Nothing>()
}
