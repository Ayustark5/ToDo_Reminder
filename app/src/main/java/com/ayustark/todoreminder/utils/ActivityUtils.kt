package com.ayustark.todoreminder.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.transition.Slide
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.Window
import androidx.activity.ComponentActivity

fun Window.setActivityAnimation() {
    requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    allowEnterTransitionOverlap = true
    enterTransition = Slide(END)
    exitTransition = Slide(START)
}

inline fun <reified T : ComponentActivity> Context.startThisActivity() {
    startActivity(
        Intent(this, T::class.java),
        ActivityOptions.makeSceneTransitionAnimation(
            this as? Activity
        ).toBundle()
    )
}