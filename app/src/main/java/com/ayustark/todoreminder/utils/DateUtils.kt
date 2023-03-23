package com.ayustark.todoreminder.utils

import java.text.SimpleDateFormat
import java.util.*

fun convertStringToDate(strDate: String, format: String = Constants.DATE_TIME_FORMAT): Date? {
    return SimpleDateFormat(format, Locale.getDefault()).parse(strDate)
}

fun convertDateToString(date: Date, format: String = Constants.DATE_TIME_FORMAT): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}