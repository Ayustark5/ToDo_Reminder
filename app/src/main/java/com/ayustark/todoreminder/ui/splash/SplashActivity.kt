package com.ayustark.todoreminder.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ayustark.todoreminder.databinding.ActivitySplashBinding
import com.ayustark.todoreminder.utils.Resource
import com.ayustark.todoreminder.utils.setActivityAnimation
import com.ayustark.todoreminder.ui.intro.IntroActivity
import com.ayustark.todoreminder.ui.login.LoginActivity
import com.ayustark.todoreminder.ui.login.LoginViewModel
import com.ayustark.todoreminder.ui.note.NoteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setActivityAnimation()
        val bind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setupUI(bind)
        subscribeToObservers()
    }

    private fun setupUI(bind: ActivitySplashBinding) {
        val initialImgAlpha = 0f
        val txtAnimationDuration = 2000L
        val imgAnimAlpha = 1f
        val imgAnimScale = 4f
        val imgAnimRotation = 2160f
        val imgAnimDuration = 5000L
        val imgAnimDelay = txtAnimationDuration / 2
        val animEndDelay = 500L

        bind.apply {
            img.alpha = initialImgAlpha
            txt.animate().alpha(0f).duration = txtAnimationDuration
            img.animate()
                .alpha(imgAnimAlpha)
                .scaleX(imgAnimScale)
                .scaleY(imgAnimScale)
                .rotationX(imgAnimRotation)
                .rotationY(imgAnimRotation)
                .setDuration(imgAnimDuration)
                .setStartDelay(imgAnimDelay)
                .withEndAction {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(animEndDelay)
                        viewModel.checkIsFirstLaunch()
                    }
                }
                .start()
        }
    }

    private fun subscribeToObservers() {
        viewModel.isFirstLaunchLiveData.observe(this@SplashActivity) {
            handleIsFirstLaunchResponse(it)
        }

        viewModel.isLoggedLiveData.observe(this@SplashActivity) {
            handleIsLoggedResponse(it)
        }
    }

    private fun handleIsFirstLaunchResponse(response: Resource<Boolean>) {
        when (response) {
            is Resource.Loading -> {
                Log.d("IsFirstLaunchResponse", "Loading")
            }
            is Resource.Error -> {
                Log.e("IsFirstLaunchResponse", "${response.message}")
                IntroActivity.startThisActivity(this@SplashActivity)
                finish()
            }
            is Resource.Success -> {
                Log.d("IsFirstLaunchResponse", "${response.data}")
                if (!response.data!!) {
                    IntroActivity.startThisActivity(this@SplashActivity)
                    finish()
                } else
                    viewModel.checkLogin()
            }
        }
    }

    private fun handleIsLoggedResponse(response: Resource<Boolean>) {
        when (response) {
            is Resource.Loading -> {
                Log.d("IsLoggedResponse", "Loading")
            }
            is Resource.Error -> {
                Log.e("IsLoggedResponse", "${response.message}")
                LoginActivity.startThisActivity(this@SplashActivity)
                finish()
            }
            is Resource.Success -> {
                if (response.data!!)
                    NoteActivity.startThisActivity(this@SplashActivity)
                else
                    LoginActivity.startThisActivity(this@SplashActivity)
                finish()
            }
        }
    }
}