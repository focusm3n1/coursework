package com.example.coursework.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.coursework.SubjectItem
import com.example.coursework.data.ScheduleRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainViewModel(private val retrofit: ScheduleRetrofit) : ViewModel() {


    private var _subjectItems = MutableLiveData<List<SubjectItem>>()
    val subjectItems: LiveData<List<SubjectItem>> = _subjectItems

    fun loadSchedule(group: String, week: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseBody = retrofit.loadSchedule(group, week).string()
            _subjectItems.postValue(parseSchedule(responseBody))
        }
    }

    private fun parseSchedule(html: String?): List<SubjectItem> {
        val subjectItems = mutableListOf<SubjectItem>()

        if (html.isNullOrEmpty()) {
            return subjectItems
        }

        val doc: Document = Jsoup.parse(html)
        val rows = doc.select("tr:has(td)")

        var currentDay = ""
        for (row in rows) {
            val columns = row.select("td")

            when (columns.size) {
                1 -> {
                    val headerDay = columns.select("span.h3 b").text().trim()
                    if (headerDay.isNotEmpty()) {
                        currentDay = headerDay
                    }
                }
                4, 6 -> {
                    val num = if (columns.size == 6) columns[0].text() else ""
                    val time = if (columns.size == 6) columns[1].text() else ""
                    val lessonType = columns[if (columns.size == 6) 2 else 0].text()
                    val lesson = columns[if (columns.size == 6) 3 else 1].text()
                    val teacher = columns[if (columns.size == 6) 4 else 2].text()
                    val aud = columns[if (columns.size == 6) 5 else 3].text()

                    val subjectItem = SubjectItem(num, time, lessonType, lesson, teacher, aud, currentDay)
                    subjectItems.add(subjectItem)
                }
            }
        }

        return subjectItems
    }

    companion object {

        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                return MainViewModel(ScheduleRetrofit.getInstance()) as T
            }
        }
    }
}