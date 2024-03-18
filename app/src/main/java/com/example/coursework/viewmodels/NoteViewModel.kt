package com.example.coursework.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.coursework.data.ScheduleDao
import com.example.coursework.data.ScheduleDatabase
import com.example.coursework.data.ScheduleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(
    private val scheduleDao: ScheduleDao,
    private val currentDay: String,
    private val lesson: String
) : ViewModel() {

    val scheduleItem = MutableLiveData<ScheduleEntity>()

    init {
        scheduleItem.value = scheduleDao.getNoteForScheduleItem(currentDay, lesson) ?: ScheduleEntity(null, currentDay, lesson, "")
    }

    fun saveScheduleItem(note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.insertScheduleEntity(
                ScheduleEntity(
                    null, currentDay, lesson, note
                )
            )
        }
    }

    companion object {

        fun Factory(currentDay: String, lesson: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>, extras: CreationExtras
                ): T {
                    val application =
                        extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] ?: error("Application is null")
                    return NoteViewModel(
                        ScheduleDatabase.getInstance(application).scheduleDao(), currentDay, lesson
                    ) as T
                }
            }
    }
}
