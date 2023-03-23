package com.ayustark.todoreminder.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayustark.todoreminder.databinding.FragmentIntroBinding
import com.ayustark.todoreminder.utils.Constants.DEFAULT_VALUE

class IntroFragment : Fragment() {

    companion object{
        const val INTRO_TITLE = "intro_title"
        const val INTRO_DESC = "intro_desc"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val bind = FragmentIntroBinding.inflate(inflater, container, false)
        arguments?.apply {
            bind.tvTitle.text = getString(INTRO_TITLE, DEFAULT_VALUE)
            bind.tvDesc.text = getString(INTRO_DESC, DEFAULT_VALUE)
        }
        return bind.root
    }
}