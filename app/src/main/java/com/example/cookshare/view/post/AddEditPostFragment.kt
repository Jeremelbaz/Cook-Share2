package com.example.cookshare.view.post

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookshare.databinding.FragmentAddEditPostBinding
import com.example.cookshare.model.Model
import com.example.cookshare.model.Post
import java.util.UUID

class AddEditPostFragment : Fragment() {

    private var _binding: FragmentAddEditPostBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgRecipePreview.setImageURI(it)
        }
    }

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
            imagePicker.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            savePost()
        }
    }

    private fun savePost() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().trim()
        val instructions = binding.etInstructions.text.toString().trim()
        val prepTime = binding.etPrepTime.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }

        binding.btnSave.isEnabled = false

        if (selectedImageUri != null) {
            Model.instance.uploadPostImage(
                imageUri = selectedImageUri!!,
                onSuccess = { imageUrl ->
                    createAndSave(title, description, ingredients, instructions, prepTime, category, notes, imageUrl)
                },
                onFailure = {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            createAndSave(title, description, ingredients, instructions, prepTime, category, notes, "")
        }
    }

    private fun createAndSave(
        title: String, description: String, ingredients: String, instructions: String,
        prepTime: String, category: String, notes: String, imageUrl: String
    ) {
        val post = Post(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            ingredients = ingredients.split("\n").filter { it.isNotBlank() },
            instructions = instructions.split("\n").filter { it.isNotBlank() },
            preparationTime = prepTime,
            category = category,
            notes = notes,
            imageUrl = imageUrl,
            userId = Model.instance.getCurrentUserId(),
            userName = Model.instance.getCurrentUserName(),
            timestamp = System.currentTimeMillis()
        )

        Model.instance.addPost(
            post = post,
            onSuccess = {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Recipe saved!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            },
            onFailure = {
                activity?.runOnUiThread {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}