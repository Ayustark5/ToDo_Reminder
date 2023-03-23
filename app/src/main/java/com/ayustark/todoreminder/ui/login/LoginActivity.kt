package com.ayustark.todoreminder.ui.login

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.ayustark.todoreminder.R
import com.ayustark.todoreminder.databinding.ActivityLoginBinding
import com.ayustark.todoreminder.utils.Constants
import com.ayustark.todoreminder.utils.Resource
import com.ayustark.todoreminder.utils.setActivityAnimation
import com.ayustark.todoreminder.utils.startThisActivity
import com.ayustark.todoreminder.ui.note.NoteActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    companion object {
        fun startThisActivity(context: Context) {
            context.startThisActivity<LoginActivity>()
        }
    }

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setActivityAnimation()
        val bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        subscribeToObservers(bind)
        setEventListeners(bind)
    }
    
    private fun subscribeToObservers(bind: ActivityLoginBinding) {
        viewModel.loginLiveData.observe(this@LoginActivity) {
            when (it) {
                is Resource.Loading -> {
                    Log.d("LoginResponse", "Loading")
                }
                is Resource.Error -> {
                    Log.e("LoginResponse", "${it.message}")
                    showToast("${it.message}")
                    checkValidationError(bind, it.message.toString())
                }
                is Resource.Success -> {
                    showToast("Login Successful")
                    NoteActivity.startThisActivity(this@LoginActivity)
                    finish()
                }
            }
        }
    }

    private fun checkValidationError(bind: ActivityLoginBinding, validator: String) {
        when (validator) {
            LoginViewModel.VALIDATION_FAILED_NAME -> {
                bind.nameLayout.error = validator
            }
            LoginViewModel.VALIDATION_FAILED_MOBILE -> {
                bind.mobileLayout.error = validator
            }
            LoginViewModel.VALIDATION_FAILED_DOB -> {
                bind.dobLayout.error = validator
            }
        }
    }

    private fun setEventListeners(bind: ActivityLoginBinding) {
        bind.apply {
            dobLayout.editText?.setOnClickListener {
                dobLayout.error = null
                showDatePickerDialog(bind)
            }
            btnLogin.setOnClickListener {
                root.clearFocus()
                val name = nameLayout.editText?.text.toString()
                val mobile = mobileLayout.editText?.text.toString()
                val dob = dobLayout.editText?.text.toString()
                viewModel.login(UserLogin(name, mobile, dob))
            }
            nameLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    nameLayout.error = null
                }else{
                    viewModel.validateLoginName(nameLayout.editText?.text.toString())
                }
            }
            mobileLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    mobileLayout.error = null
                }else{
                    viewModel.validateLoginMobile(mobileLayout.editText?.text.toString())
                }
            }
            tvCheckApi.setOnClickListener{
                PocketActivity.startThisActivity(this@LoginActivity)
            }
        }
    }

    private fun showDatePickerDialog(bind: ActivityLoginBinding) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this@LoginActivity, { _, dobYear, dobMonth, dobDay ->

            bind.dobLayout.editText?.setText(
                getString(
                    R.string.dob_str,
                    dobDay,
                    dobMonth + 1,
                    dobYear
                )
            )//"$dayOfMonth/$monthOfYear/$year")

        }, year, month, day)
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.datePicker.minDate = Calendar.getInstance().let {
            it.set(year.minus(Constants.MAX_AGE), month, day)
            it.timeInMillis
        }

        dpd.show()
    }

    private fun showToast(msg: String?) {
        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT)
            .show()
    }
}