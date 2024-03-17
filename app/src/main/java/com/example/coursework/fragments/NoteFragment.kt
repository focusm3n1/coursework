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

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<NoteFragmentArgs>()
    private val currentDay by lazy { args.currentDay }
    private val lesson by lazy { args.lesson }
    private val viewModel: NoteViewModel by viewModels { NoteViewModel.Factory(currentDay, lesson) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(layoutInflater, container, false)

        binding.currentDayTextView.text = currentDay
        binding.lessonTextView.text = lesson

        viewModel.scheduleItem.observe(viewLifecycleOwner) {
            binding.editText.setText(it.note)
        }

        binding.okButton.setOnClickListener {
            viewModel.saveScheduleItem(binding.editText.text.toString())
            showNoteSavedNotification()
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNoteSavedNotification() {
        Toast.makeText(context, "Заметка успешно сохранена", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(currentDay: String, lesson: String): NoteFragment {
            val fragment = NoteFragment()
            val args = Bundle().apply {
                putString("currentDay", currentDay)
                putString("lesson", lesson)
            }
            fragment.arguments = args
            return fragment
        }
    }
}