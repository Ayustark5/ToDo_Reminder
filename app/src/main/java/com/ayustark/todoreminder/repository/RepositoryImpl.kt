package com.ayustark.todoreminder.repository

import android.content.SharedPreferences
import android.util.Log
import com.ayustark.todoreminder.utils.*
import com.ayustark.todoreminder.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.InterruptedIOException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val apiService: ApiService
) :
    Repository {
    override fun login() = flow {
        emit(sharedPreferences.setLogged(true))
    }

    override fun checkLogin() = flow {
        emit(sharedPreferences.checkLogged())
    }

    override fun checkIsFirstLaunch() = flow {
        emit(sharedPreferences.checkIsFirstLaunch())
    }

    override fun updateIsFirstLaunch() = flow {
        emit(sharedPreferences.updateIsFirstLaunch(true))
    }

    override fun logout() = flow {
        emit(sharedPreferences.setLogged(false))
    }

    override fun getPersonalizationData() = flow {
        emit(handleApiResponse { apiService.getPocketCategoryPage() })
    }
}

suspend inline fun <reified T> handleApiResponse(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline apiCall: suspend () -> Response<T>
): Resource<T> {
    return withContext(dispatcher) {
        try {
            val response = apiCall.invoke()
            when {
                response.isSuccessful -> {
                    Log.d("ApiResponse", "Response obtained is ${response.body().toString()}")
                    Resource.Success(response.body())
                }
                /*response.code() == HTTP_CODE_UNAUTHORIZED -> {
                    Resource.UnAuthenticated("Session Expired. Please Login")
                }*/
                else -> {
                    Log.e("ApiResponse", "Error found")
                    handleApiError(response)
                }
            }
        } catch (e: InterruptedIOException) {
            Log.e("ApiResponse", "Exception is ${e.message}", e)
            Resource.Error(
                e.message ?: "ApiError",
                null,
                null
            )
        } catch (e: Exception) {
            when {
                e.message != null -> {
                    Log.e("ApiResponse", "Exception is ${e.message}", e)
                    Resource.Error(e.message ?: "ApiError", null)
                }
                else -> {
                    Log.e("ApiResponse", "An unknown error occurred")
                    Resource.Error("An unknown error occurred", null)
                }
            }
        }
    }
}

fun <T> handleApiError(response: Response<T>): Resource.Error<T> {
//    val type = object : TypeToken<ApiError>() {}.type
//    val reader = response.errorBody()?.charStream()
//    val floApiError: ApiError = Gson().fromJson(reader, type)
//    Log.d("ApiError", "Passed $floApiError")
//    val formattedMessage =
//        "${floApiError.data.errorMessage})."
    Log.e("ApiError", "Error ${response.code()}")
    return Resource.Error("Error ${response.code()}")
}