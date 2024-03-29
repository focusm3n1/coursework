package com.example.coursework.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.DayItem
import com.example.coursework.adapters.ScheduleAdapter
import com.example.coursework.databinding.FragmentMainBinding
import com.example.coursework.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var textDate: TextView
    private lateinit var textGroup: TextView
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory() }
    private var currentGroup: String? = null
    private var currentWeek: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        textDate = binding.textDate
        textGroup = binding.textGroup
        setCurrentWeek()

        binding.iconLeft.setOnClickListener {
            loadNextWeek(false)
        }

        binding.iconRight.setOnClickListener {
            loadNextWeek(true)
        }

        binding.textGroup.setOnClickListener {
            showGroupInputDialog()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.subjectItems.observe(viewLifecycleOwner) { scheduleItems ->
            val daysWithSubjects = scheduleItems.groupBy { it.currentDay }
                .map { (day, subjects) -> DayItem(day, subjects) }
            updateScheduleUI(daysWithSubjects)
        }

        if (binding.textGroup.text == "Введите группу") {
            blinkAnimation(binding.textGroup)
        }
    }

    private fun blinkAnimation(view: View) {
        val blinkAnimation = AlphaAnimation(0.0f, 1.0f)
        blinkAnimation.duration = 500
        blinkAnimation.repeatCount = 5
        blinkAnimation.repeatMode = Animation.REVERSE
        view.startAnimation(blinkAnimation)
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
            textGroup.text = "Группа $currentGroup"
            updateSchedule(currentGroup)
        }

        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateSchedule(newGroup: String?) {
        newGroup?.let {
            loadSchedule(it, currentWeek)
        }
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
        }
    }

    private fun updateScheduleUI(dayItems: List<DayItem>) {
        val recyclerView: RecyclerView = binding.scheduleRecyclerView
        val adapter = ScheduleAdapter(dayItems, action)
        recyclerView.adapter = adapter
        recyclerView.setVisible(dayItems.isNotEmpty())
        binding.noClassesTextView.setVisible(dayItems.isEmpty())
    }

    private val action = object : ActionInterface {
        override fun onButtonClick(currentDay: String, lesson: String) {
            val noteDialog = NoteFragment.newInstance(currentDay, lesson)
            noteDialog.show(parentFragmentManager, "note_dialog")
        }
    }

}

interface ActionInterface {
    fun onButtonClick(currentDay: String, lesson: String)
}

fun View.setVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}
