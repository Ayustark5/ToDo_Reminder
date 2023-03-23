package com.ayustark.todoreminder.network.responses

import com.google.gson.annotations.SerializedName

data class Personalization(
    @SerializedName("personalizationSequence") val personalizationSequence: List<PersonalizationSequence>,
    @SerializedName("personalizationType") val personalizationType: String
)

data class PersonalizationSequence(
//    @SerializedName("jsonURL") val jsonURL: String,
//    @SerializedName("priority") val priority: Int,
    @SerializedName("widget_id") val widgetId: String,
    @SerializedName("widget_name") val widgetName: String
)