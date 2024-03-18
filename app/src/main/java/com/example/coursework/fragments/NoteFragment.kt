package com.example.coursework.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.coursework.databinding.FragmentNoteBinding
import com.example.coursework.viewmodels.NoteViewModel

class NoteFragment : DialogFragment() {

    private var binding: FragmentNoteBinding? = null
    private val args by navArgs<NoteFragmentArgs>()
    private val currentDay by lazy { args.currentDay }
    private val lesson by lazy { args.lesson }
    private val viewModel: NoteViewModel by viewModels { NoteViewModel.Factory(currentDay, lesson) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            currentDayTextView.text = currentDay
            lessonTextView.text = lesson

            viewModel.scheduleItem.observe(viewLifecycleOwner) {
                editText.setText(it.note)
            }

            okButton.setOnClickListener {
                viewModel.saveScheduleItem(editText.text.toString())
                showNoteSavedNotification()
                dismiss()
            }

            cancelButton.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showNoteSavedNotification() {
        Toast.makeText(requireContext(), "Заметка успешно сохранена", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(currentDay: String, lesson: String): NoteFragment {
            return NoteFragment().apply {
                arguments = Bundle().apply {
                    putString("currentDay", currentDay)
                    putString("lesson", lesson)
                }
            }
        }
    }
}
