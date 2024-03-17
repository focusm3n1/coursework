package com.example.coursework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.DayItem
import com.example.coursework.R
import com.example.coursework.fragments.ActionInterface

class ScheduleAdapter(
    private var dayItems: List<DayItem>,
    private val action: ActionInterface
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weekDayTextView: TextView = itemView.findViewById(R.id.weekDay)
        val subjectsRecyclerView: RecyclerView = itemView.findViewById(R.id.subjectsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dayItem = dayItems[position]

        holder.weekDayTextView.text = dayItem.currentDay

        val subjectsAdapter = SubjectAdapter(dayItem.subjects, action)
        holder.subjectsRecyclerView.apply {
            adapter = subjectsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getItemCount(): Int {
        return dayItems.size
    }

}

