package com.example.coursework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.fragments.ActionInterface

class ScheduleAdapter(
    private val scheduleItems: List<ScheduleItem>,
    private val action: ActionInterface
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numTextView: TextView = itemView.findViewById(R.id.num)
        val timeTextView: TextView = itemView.findViewById(R.id.time)
        val typeTextView: TextView = itemView.findViewById(R.id.lessonType)
        val subjectTextView: TextView = itemView.findViewById(R.id.lesson)
        val teacherTextView: TextView = itemView.findViewById(R.id.teacher)
        val audTextView: TextView = itemView.findViewById(R.id.aud)
        val weekDayTextView: TextView = itemView.findViewById(R.id.weekDay)
        val addNoteButton: Button = itemView.findViewById(R.id.addNoteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.subject_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = scheduleItems[position]

        // Устанавливаем значения в TextView из данных
        holder.numTextView.text = item.num
        holder.timeTextView.text = item.time
        holder.typeTextView.text = item.lessonType
        holder.subjectTextView.text = item.lesson
        holder.teacherTextView.text = item.teacher
        holder.audTextView.text = item.aud
        holder.weekDayTextView.text = item.currentDay
        if (item.currentDay.endsWith(" ")) {
            holder.weekDayTextView.visibility = View.GONE
        }

        var isAddNoteButtonVisible = false

        holder.itemView.setOnClickListener {
            it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }

            if (isAddNoteButtonVisible) {
                holder.addNoteButton.visibility = View.GONE
            } else {
                holder.addNoteButton.visibility = View.VISIBLE
            }

            isAddNoteButtonVisible = !isAddNoteButtonVisible
        }

        holder.addNoteButton.setOnClickListener {
            action.onButtonClick(item.currentDay, item.lesson)
        }

    }

    override fun getItemCount(): Int {
        return scheduleItems.size
    }
}
