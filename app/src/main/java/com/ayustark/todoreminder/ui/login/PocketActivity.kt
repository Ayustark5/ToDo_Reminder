package com.ayustark.todoreminder.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.ayustark.todoreminder.databinding.ActivityPocketBinding
import com.ayustark.todoreminder.utils.Resource
import com.ayustark.todoreminder.utils.startThisActivity
import com.ayustark.todoreminder.ui.adapters.PersonalizationAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PocketActivity : AppCompatActivity() {

    companion object {
        fun startThisActivity(context: Context) {
            context.startThisActivity<PocketActivity>()
        }
    }

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityPocketBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setupRecyclerView(bind)
        subscribeToObservers(bind)
        viewModel.getPersonalizationData()
    }

    private fun subscribeToObservers(bind: ActivityPocketBinding) {
        viewModel.pocketCategoryLiveData.observe(this@PocketActivity) {
            when (it) {
                is Resource.Loading -> {
                    Log.d("pocketCategoryLiveDataResponse", "Loading")
                }
                is Resource.Error -> {
                    Log.e("pocketCategoryLiveDataResponse", "${it.message}")
                }
                is Resource.Success -> {
                    Log.d("pocketCategoryLiveDataResponse", "${it.data}")
                    it.data?.personalizationSequence?.let { itemList ->
                        (bind.rvPersonalization.adapter as PersonalizationAdapter).setItemList(
                            itemList
                        )
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(bind: ActivityPocketBinding) {
        bind.rvPersonalization.layoutManager = LinearLayoutManager(this@PocketActivity)
        bind.rvPersonalization.adapter = PersonalizationAdapter()
        bind.rvPersonalization.addItemDecoration(
            DividerItemDecoration(
                bind.rvPersonalization.context,
                VERTICAL
            )
        )
    }
}