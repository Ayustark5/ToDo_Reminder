package com.ayustark.todoreminder.utils

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null,
    val errorData: Exception? = null
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null, errorData: java.lang.Exception? = null) :
        Resource<T>(data, message, errorData)

    class Loading<T>(data: T? = null) : Resource<T>(data)
}