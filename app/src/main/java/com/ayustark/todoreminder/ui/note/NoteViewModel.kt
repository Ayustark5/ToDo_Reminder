package com.ayustark.todoreminder.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayustark.todoreminder.db.NoteEntity
import com.ayustark.todoreminder.utils.Resource
import com.ayustark.todoreminder.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _logoutLiveData = MutableLiveData<Resource<Boolean>>()
    val logoutLiveData: LiveData<Resource<Boolean>> = _logoutLiveData

    val noteList: ArrayList<NoteEntity> = arrayListOf(
        NoteEntity("fg", "fdggdfg", "fgdg", "fdgdg"),
        NoteEntity("gdgdfg", "njnjnkiu", "dfgfdg", "fgfdg"),
        NoteEntity("erer", "hyhy", "vv", "bgb"),
        NoteEntity("jujn", "lkl", "uiu", ""),
        NoteEntity("uyyu", "jmn", "ijnj", "knkjh"),
        NoteEntity("fg", "fdggdfg", "fgdg", "fdgdg"),
        NoteEntity("gdgdfg", "njnjnkiu", "dfgfdg", "fgfdg"),
        NoteEntity("erer", "hyhy", "vv", "bgb"),
        NoteEntity("jujn", "lkl", "uiu", ""),
        NoteEntity("uyyu", "jmn", "ijnj", "knkjh"), NoteEntity("fg", "fdggdfg", "fgdg", "fdgdg"),
        NoteEntity("gdgdfg", "njnjnkiu", "dfgfdg", "fgfdg"),
        NoteEntity("erer", "hyhy", "vv", "bgb"),
        NoteEntity("jujn", "lkl", "uiu", ""),
        NoteEntity("uyyu", "jmn", "ijnj", "knkjh"), NoteEntity("fg", "fdggdfg", "fgdg", "fdgdg"),
        NoteEntity("gdgdfg", "njnjnkiu", "dfgfdg", "fgfdg"),
        NoteEntity("erer", "hyhy", "vv", "bgb"),
        NoteEntity("jujn", "lkl", "uiu", ""),
        NoteEntity("uyyu", "jmn", "ijnj", "knkjh"),
    )

    fun logout() {
        viewModelScope.launch {
            repository.logout().onStart {
                _logoutLiveData.value = Resource.Loading()
            }.catch { error ->
                _logoutLiveData.value = error.message?.let { errorMsg -> Resource.Error(errorMsg) }
            }.collect {
                _logoutLiveData.value = Resource.Success(true)
            }
        }
    }


}