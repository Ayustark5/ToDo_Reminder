package com.ayustark.todoreminder.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayustark.todoreminder.utils.*
import com.ayustark.todoreminder.network.responses.Personalization
import com.ayustark.todoreminder.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    companion object {
        const val VALIDATION_FAILED_NAME = "First Name and Last Name required"
        const val VALIDATION_FAILED_MOBILE = "10 digit mobile number required"
        const val VALIDATION_FAILED_DOB = "Max ${Constants.MAX_AGE} yrs. old"
    }

    private val _loginLiveData by lazy { MutableLiveData<Resource<Boolean>>() }
    val loginLiveData: LiveData<Resource<Boolean>> by lazy { _loginLiveData }

    private val _isLoggedLiveData by lazy { MutableLiveData<Resource<Boolean>>() }
    val isLoggedLiveData: LiveData<Resource<Boolean>> by lazy { _isLoggedLiveData }

    private val _isFirstLaunchLiveData by lazy { MutableLiveData<Resource<Boolean>>() }
    val isFirstLaunchLiveData: LiveData<Resource<Boolean>> by lazy { _isFirstLaunchLiveData }

    private val _updateIsFirstLaunchLiveData by lazy { MutableLiveData<Resource<Boolean>>() }
    val updateIsFirstLaunchLiveData: LiveData<Resource<Boolean>> by lazy { _updateIsFirstLaunchLiveData }

    private val _pocketCategoryLiveData by lazy { MutableLiveData<Resource<Personalization>>() }
    val pocketCategoryLiveData: LiveData<Resource<Personalization>> by lazy { _pocketCategoryLiveData }

    fun login(userData: UserLogin) {
        viewModelScope.launch {
            var success = true
            if (!validateLoginName(userData.name)) {
                success = false
            }
            if (!validateLoginMobile(userData.mobile)) {
                success = false
            }
            if (!validateLoginDob(userData.dob)) {
                success = false
            }
            if (success) {
                repository.login().onStart {
                    _loginLiveData.value = Resource.Loading()
                }.catch { error ->
                    _loginLiveData.value =
                        error.message?.let { errorMsg -> Resource.Error(errorMsg) }
                }.collect {
                    _loginLiveData.value = Resource.Success(true)
                }
            }
        }
    }

    fun validateLoginName(name: String): Boolean{
        if (!validateName(name)) {
            _loginLiveData.value = Resource.Error(VALIDATION_FAILED_NAME, false)
            return false
        }
        return true
    }

    fun validateLoginMobile(mobile: String): Boolean{
        if (!validateMobile(mobile)) {
            _loginLiveData.value = Resource.Error(VALIDATION_FAILED_MOBILE, false)
            return false
        }
        return true
    }

    fun validateLoginDob(dob: String): Boolean{
        if (!validateDOB(dob)) {
            _loginLiveData.value = Resource.Error(VALIDATION_FAILED_DOB, false)
            return true
        }
        return false
    }

    fun checkIsFirstLaunch() {
        viewModelScope.launch {
            repository.checkIsFirstLaunch().onStart {
                _isFirstLaunchLiveData.value = Resource.Loading()
            }.catch { error ->
                _isFirstLaunchLiveData.value =
                    error.message?.let { errorMsg -> Resource.Error(errorMsg) }
            }.collect {
                _isFirstLaunchLiveData.value = Resource.Success(it)
            }
        }
    }

    fun checkLogin() {
        viewModelScope.launch {
            repository.checkLogin().onStart {
                _isLoggedLiveData.value = Resource.Loading()
            }.catch { error ->
                _isLoggedLiveData.value =
                    error.message?.let { errorMsg -> Resource.Error(errorMsg) }
            }.collect {
                _isLoggedLiveData.value = Resource.Success(it)
            }
        }
    }

    fun updateIsFirstLaunch() {
        viewModelScope.launch {
            repository.updateIsFirstLaunch().onStart {
                _updateIsFirstLaunchLiveData.value = Resource.Loading()
            }.catch { error ->
                _updateIsFirstLaunchLiveData.value =
                    error.message?.let { errorMsg -> Resource.Error(errorMsg) }
            }.collect {
                _updateIsFirstLaunchLiveData.value = Resource.Success(true)
            }
        }
    }

    fun getPersonalizationData() {
        viewModelScope.launch {
            repository.getPersonalizationData().onStart {
                _pocketCategoryLiveData.value = Resource.Loading()
            }.catch { error ->
                _pocketCategoryLiveData.value =
                    error.message?.let { errorMsg -> Resource.Error(errorMsg) }
            }.collect {
                _pocketCategoryLiveData.value = Resource.Success(it.data)
            }
        }
    }

}