package com.example.coursework.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.coursework.ScheduleItem
import com.example.coursework.data.ScheduleRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainViewModel(private val retrofit: ScheduleRetrofit) : ViewModel() {


    private var _scheduleItems = MutableLiveData<List<ScheduleItem>>()
    val scheduleItems: LiveData<List<ScheduleItem>> = _scheduleItems

    fun loadSchedule(group: String, week: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseBody = retrofit.loadSchedule(group, week).string()
            _scheduleItems.postValue(parseHtmlForSchedule(responseBody))
        }
    }

    private fun parseHtmlForSchedule(html: String?): List<ScheduleItem> {
        val scheduleItems = mutableListOf<ScheduleItem>()

        if (!html.isNullOrEmpty()) {
            val doc: Document = Jsoup.parse(html)

            val rows = doc.select("tr:has(td)")

            var currentDay = ""
            for (row in rows) {
                val columns = row.select("td")

                if (columns.size == 1) {
                    val headerDay = columns.select("span.h3 b")
                    if (headerDay.isNotEmpty()) {
                        currentDay = headerDay.text().trim()
                    }
                } else if (columns.size == 6) {
                    val num = columns[0].text()
                    val time = columns[1].text()
                    val lessonType = columns[2].text()
                    val lesson = columns[3].text()
                    val teacher = columns[4].text()
                    val aud = columns[5].text()

                    val scheduleItem =
                        ScheduleItem(num, time, lessonType, lesson, teacher, aud, currentDay)
                    scheduleItems.add(scheduleItem)
                    currentDay = "$currentDay "
                } else if (columns.size == 4) {
                    val lessonType = columns[0].text()
                    val lesson = columns[1].text()
                    val teacher = columns[2].text()
                    val aud = columns[3].text()

                    val scheduleItem =
                        ScheduleItem("", "", lessonType, lesson, teacher, aud, currentDay)
                    scheduleItems.add(scheduleItem)
                }
            }
        }

        return scheduleItems
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