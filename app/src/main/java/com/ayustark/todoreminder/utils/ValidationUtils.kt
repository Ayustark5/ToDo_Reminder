package com.ayustark.todoreminder.utils

import android.util.Patterns


fun validateName(name: String): Boolean {
    if (Regex("^([a-zA-Z]{2,}\\s[a-zA-Z]{2,}\\s?[a-zA-Z]*)").matches(name))
        return true
    return false
}

fun validateMobile(mobile: String): Boolean {
    if (Patterns.PHONE.matcher(mobile).matches())
        return true
    return false
}

fun validateDOB(dob:  String): Boolean {
    if (dob.length in 8..10)
        return true
    return false
}