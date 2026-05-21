package com.example.cookshare.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cookshare.databinding.FragmentAddEditPostBinding

class AddEditPostFragment : Fragment() {

    private var _binding: FragmentAddEditPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectImage.setOnClickListener {
            // TODO: Open camera / gallery
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val ingredients = binding.etIngredients.text.toString().trim()
            val instructions = binding.etInstructions.text.toString().trim()
            val prepTime = binding.etPrepTime.text.toString().trim()
            val category = binding.etCategory.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Title is required"
                return@setOnClickListener
            }

            // TODO: Save post to Firestore
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}