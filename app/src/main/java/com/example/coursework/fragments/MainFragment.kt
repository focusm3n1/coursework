package com.example.coursework.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.ScheduleAdapter
import com.example.coursework.ScheduleItem
import com.example.coursework.databinding.FragmentMainBinding
import com.example.coursework.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var textDate: TextView
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory() }
    private var currentGroup: String? = null
    private lateinit var textGroup: TextView
    private var currentWeek: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        textDate = binding.textDate
        setCurrentWeek()
        loadSchedule(currentGroup, currentWeek)
        binding.iconLeft.setOnClickListener {
            loadNextWeek(false)
        }

        binding.iconRight.setOnClickListener {
            loadNextWeek(true)
        }

        binding.textGroup.setOnClickListener {
            showGroupInputDialog()
        }

        textDate.setOnClickListener {
            showCalendar()
        }

        return binding.root
    }

    private fun showGroupInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Введите группу")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        currentGroup?.let { input.setText(it) }
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            currentGroup = input.text.toString()
            textGroup.text =
                "Группа $currentGroup"
            updateScheduleForGroup(currentGroup)
        }

        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.show()
    }


    private fun updateScheduleForGroup(newGroup: String?) {
        newGroup?.let {
            loadSchedule(it, currentWeek)
        }
    }

    private fun showCalendar() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.timeInMillis = 0
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, day)

                // Находим первый день недели (понедельник)
                selectedCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

                // Переходим к началу выбранной недели
                while (selectedCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    selectedCalendar.add(Calendar.DATE, -1)
                }

                // Форматируем начало и конец выбранной недели в формат "ddMMyyyy"
                val formatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                val startOfWeek = formatter.format(selectedCalendar.time)
                selectedCalendar.add(Calendar.DATE, 6)
                val endOfWeek = formatter.format(selectedCalendar.time)

                // Обновляем переменную currentWeek
                currentWeek = startOfWeek + endOfWeek
                Log.d("MainFragment", "Selected Week: $currentWeek")

                // Обновляем текст в TextView с датой
                textDate.text = "$startOfWeek - $endOfWeek"

                // Перезагружаем расписание для выбранной недели
                loadSchedule(currentGroup, currentWeek)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }


    private fun setCurrentWeek() {
        val formatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time

        val startCalendar = Calendar.getInstance()
        startCalendar.time = currentDate
        startCalendar.firstDayOfWeek = Calendar.MONDAY
        startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val endCalendar = Calendar.getInstance()
        endCalendar.time = startCalendar.time
        endCalendar.firstDayOfWeek = Calendar.MONDAY
        endCalendar.add(Calendar.DATE, 6)

        val startDateString = formatter.format(startCalendar.time)
        val endDateString = formatter.format(endCalendar.time)

        currentWeek = "$startDateString$endDateString"

        val currentDateString = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(startCalendar.time)
        val endDateFormattedString = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(endCalendar.time)

        textDate.text = "$currentDateString - $endDateFormattedString"
        textGroup = binding.textGroup
        currentGroup?.let {
            textGroup.text = "Группа $it"
        } ?: run {
            textGroup.text = "Введите группу"
        }
    }


    private fun loadNextWeek(isNext: Boolean) {
        val formatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val startDate = formatter.parse(currentWeek.substring(0, 8))
        val endDate = formatter.parse(currentWeek.substring(8))

        val startCalendar = Calendar.getInstance()
        startCalendar.time = startDate

        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        if (isNext) {
            startCalendar.add(Calendar.DATE, 7)
            endCalendar.add(Calendar.DATE, 7)
        } else {
            startCalendar.add(Calendar.DATE, -7)
            endCalendar.add(Calendar.DATE, -7)
        }

        val newStartDate = formatter.format(startCalendar.time)
        val newEndDate = formatter.format(endCalendar.time)

        currentWeek = "$newStartDate$newEndDate"

        val currentDateString =
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(startCalendar.time)
        val endDateString =
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(endCalendar.time)

        textDate.text = "$currentDateString - $endDateString"


        loadSchedule(currentGroup, currentWeek)
    }



    private fun loadSchedule(group: String?, week: String) {
        group?.let {
            viewModel.loadSchedule(it, week)
            viewModel.scheduleItems.observe(viewLifecycleOwner) {
                updateScheduleUI(it)
            }
        }
    }

    private fun updateScheduleUI(scheduleItems: List<ScheduleItem>) {
        val recyclerView: RecyclerView = binding.scheduleRecyclerView
        if (scheduleItems.isEmpty()) {
            val noClassesTextView: TextView = binding.noClassesTextView
            noClassesTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            val adapter = ScheduleAdapter(scheduleItems, action)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val noClassesTextView: TextView = binding.noClassesTextView
            noClassesTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private val action = object : ActionInterface {
        override fun onButtonClick(currentDay: String, lesson: String) {
            val NoteDialog = NoteFragment.newInstance(currentDay, lesson)
            NoteDialog.show(parentFragmentManager, "note_dialog")
        }
    }

}

interface ActionInterface {

    fun onButtonClick(currentDay: String, lesson: String)
}
