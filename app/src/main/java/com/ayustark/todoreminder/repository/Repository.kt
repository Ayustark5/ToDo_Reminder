package com.ayustark.todoreminder.repository

import com.ayustark.todoreminder.utils.Resource
import com.ayustark.todoreminder.network.responses.Personalization
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun login(): Flow<Unit>

    fun checkLogin(): Flow<Boolean>

    fun checkIsFirstLaunch(): Flow<Boolean>

    fun updateIsFirstLaunch(): Flow<Unit>

    fun logout(): Flow<Unit>

    fun getPersonalizationData(): Flow<Resource<Personalization>>

}