package com.ayustark.todoreminder.utils

import android.content.SharedPreferences
import com.ayustark.todoreminder.utils.Constants.IS_FIRST_LAUNCH
import com.ayustark.todoreminder.utils.Constants.IS_LOGGED

fun SharedPreferences.setLogged(isLogged: Boolean){
    edit().putBoolean(IS_LOGGED, isLogged).apply()
}

fun SharedPreferences.checkLogged(): Boolean{
    return getBoolean(IS_LOGGED, false)
}

fun SharedPreferences.checkIsFirstLaunch(): Boolean{
    return getBoolean(IS_FIRST_LAUNCH, false)
}

fun SharedPreferences.updateIsFirstLaunch(isFirstLaunch: Boolean){
    edit().putBoolean(IS_FIRST_LAUNCH, isFirstLaunch).apply()
}