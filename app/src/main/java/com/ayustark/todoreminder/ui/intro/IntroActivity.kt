package com.ayustark.todoreminder.ui.intro

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.airbnb.paris.extensions.style
import com.ayustark.todoreminder.R
import com.ayustark.todoreminder.databinding.ActivityAppIntroBinding
import com.ayustark.todoreminder.utils.Resource
import com.ayustark.todoreminder.utils.setActivityAnimation
import com.ayustark.todoreminder.utils.startThisActivity
import com.ayustark.todoreminder.ui.adapters.IntroViewPagerAdapter
import com.ayustark.todoreminder.ui.login.LoginActivity
import com.ayustark.todoreminder.ui.login.LoginViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    companion object {
        fun startThisActivity(context: Context) {
            context.startThisActivity<LoginActivity>()
        }
    }

    private var pageChangeJob: Job? = null

    private val viewPagerPageChangeDelay = 3000L

    private val viewModel: LoginViewModel by viewModels()
    val fragmentList = listOf(IntroFragment().apply {
        arguments = Bundle()
        arguments?.putString(IntroFragment.INTRO_TITLE, "Deadline")
        arguments?.putString(
            IntroFragment.INTRO_DESC, "Here, you have an option to add due date for your TODOs'"
        )
    }, IntroFragment().apply {
        arguments = Bundle()
        arguments?.putString(IntroFragment.INTRO_TITLE, "Sortable")
        arguments?.putString(
            IntroFragment.INTRO_DESC,
            "We are giving you the ease of sorting through your TODOs'"
        )
    }, IntroFragment().apply {
        arguments = Bundle()
        arguments?.putString(IntroFragment.INTRO_TITLE, "Editable")
        arguments?.putString(
            IntroFragment.INTRO_DESC, "You can edit any TODO at any point of time you want"
        )
    }, IntroFragment().apply {
        arguments = Bundle()
        arguments?.putString(IntroFragment.INTRO_TITLE, "Eye Friendly")
        arguments?.putString(
            IntroFragment.INTRO_DESC,
            "We have used eye friendly colours so that adding and going through your todos' doesn't put strain on your eyes"
        )
    }, IntroFragment().apply {
        arguments = Bundle()
        arguments?.putString(IntroFragment.INTRO_TITLE, "Login")
        arguments?.putString(
            IntroFragment.INTRO_DESC,
            "User data based login so that only you can access your todos'"
        )
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setActivityAnimation()
        val bind = ActivityAppIntroBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setupViewPagerAndTabLayout(bind)
        subscribeToObservers()
        setupEventListeners(bind)
    }

    private fun subscribeToObservers() {
        viewModel.updateIsFirstLaunchLiveData.observe(this@IntroActivity) {
            when (it) {
                is Resource.Loading -> {
                    Log.d("updateIsFirstLaunchResponse", "Loading")
                }
                is Resource.Error -> {
                    Log.e("updateIsFirstLaunchResponse", "${it.message}")
                    LoginActivity.startThisActivity(this@IntroActivity)
                    finish()
                }
                is Resource.Success -> {
                    LoginActivity.startThisActivity(this@IntroActivity)
                    finish()
                }
            }
        }
    }

    private fun setupEventListeners(bind: ActivityAppIntroBinding) {
        bind.btnSkipNext.setOnClickListener {
            pageChangeJob?.cancel()
            viewModel.updateIsFirstLaunch()
        }
    }

    private fun setupViewPagerAndTabLayout(bind: ActivityAppIntroBinding) {
        val adapter = IntroViewPagerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragments(fragmentList)
        bind.apply {
            vpIntro.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            vpIntro.adapter = adapter
            vpIntro.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == fragmentList.lastIndex) btnSkipNext.style(R.style.Widget_NextBtn_Filled)
                    else {
                        btnSkipNext.style(R.style.Widget_SkipBtn_OutlinedTransparent)
                        moveToNextPage(bind)
                    }
                    super.onPageSelected(position)
                }
            })
            TabLayoutMediator(tlIndicator, vpIntro, false, true) { _, _ ->
            }.attach()
            moveToNextPage(bind)
        }
    }

    private fun moveToNextPage(bind: ActivityAppIntroBinding) {
        pageChangeJob?.cancel()
        pageChangeJob = CoroutineScope(Dispatchers.Main).launch {
            delay(viewPagerPageChangeDelay)
            bind.vpIntro.setCurrentItem(bind.vpIntro.currentItem + 1, true)
        }
    }


}