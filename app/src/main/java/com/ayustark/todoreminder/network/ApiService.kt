package com.ayustark.todoreminder.network

import com.ayustark.todoreminder.network.responses.Personalization
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("homepage/homepage-bfl/android/android_homepage_bfl.json")
    suspend fun getPocketCategoryPage(): Response<Personalization>

}