package com.example.coursework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.R
import com.example.coursework.SubjectItem
import com.example.coursework.fragments.ActionInterface

class SubjectAdapter(
    private val subjectItems: List<SubjectItem>,
    private val action: ActionInterface
) : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numTextView: TextView = itemView.findViewById(R.id.num)
        val timeTextView: TextView = itemView.findViewById(R.id.time)
        val typeTextView: TextView = itemView.findViewById(R.id.lessonType)
        val subjectTextView: TextView = itemView.findViewById(R.id.lesson)
        val teacherTextView: TextView = itemView.findViewById(R.id.teacher)
        val audTextView: TextView = itemView.findViewById(R.id.aud)
        val addNoteButton: Button = itemView.findViewById(R.id.addNoteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.subject_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = subjectItems[position]

        holder.numTextView.text = item.num
        holder.timeTextView.text = item.time
        holder.typeTextView.text = item.lessonType
        holder.subjectTextView.text = item.lesson
        holder.teacherTextView.text = item.teacher
        holder.audTextView.text = item.aud

        var isAddNoteButtonVisible = false

        holder.itemView.setOnClickListener {
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
        return subjectItems.size
    }
}
