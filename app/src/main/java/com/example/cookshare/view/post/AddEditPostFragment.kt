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
import androidx.navigation.fragment.navArgs
import com.example.cookshare.databinding.FragmentAddEditPostBinding
import com.example.cookshare.model.Model
import com.example.cookshare.model.Post
import com.squareup.picasso.Picasso
import java.util.UUID

class AddEditPostFragment : Fragment() {

    private var _binding: FragmentAddEditPostBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditPostFragmentArgs by navArgs()
    private var selectedImageUri: Uri? = null
    private var existingPost: Post? = null

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

        if (args.postId.isNotEmpty()) {
            loadExistingPost(args.postId)
            binding.btnSave.text = "Update Recipe"
        }
    }

    private fun loadExistingPost(postId: String) {
        Model.instance.getAllPostsLocal().observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId } ?: return@observe

            if (post.userId != Model.instance.getCurrentUserId()) {
                Toast.makeText(requireContext(), "You can only edit your own recipes", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@observe
            }

            existingPost = post
            binding.etTitle.setText(post.title)
            binding.etDescription.setText(post.description)
            binding.etIngredients.setText(post.ingredients.joinToString("\n"))
            binding.etInstructions.setText(post.instructions.joinToString("\n"))
            binding.etPrepTime.setText(post.preparationTime)
            binding.etCategory.setText(post.category)
            binding.etNotes.setText(post.notes)

            if (post.imageUrl.isNotEmpty()) {
                Picasso.get().load(post.imageUrl).into(binding.imgRecipePreview)
            }
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

        setLoading(true)

        if (selectedImageUri != null) {
            Model.instance.uploadPostImage(
                imageUri = selectedImageUri!!,
                onSuccess = { imageUrl ->
                    createAndSave(title, description, ingredients, instructions, prepTime, category, notes, imageUrl)
                },
                onFailure = {
                    setLoading(false)
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            val imageUrl = existingPost?.imageUrl ?: ""
            createAndSave(title, description, ingredients, instructions, prepTime, category, notes, imageUrl)
        }
    }

    private fun createAndSave(
        title: String, description: String, ingredients: String, instructions: String,
        prepTime: String, category: String, notes: String, imageUrl: String
    ) {
        val isEdit = existingPost != null
        val post = Post(
            id = existingPost?.id ?: UUID.randomUUID().toString(),
            title = title,
            description = description,
            ingredients = ingredients.split("\n").filter { it.isNotBlank() },
            instructions = instructions.split("\n").filter { it.isNotBlank() },
            preparationTime = prepTime,
            category = category,
            notes = notes,
            imageUrl = imageUrl,
            userId = existingPost?.userId ?: Model.instance.getCurrentUserId(),
            userName = existingPost?.userName ?: Model.instance.getCurrentUserName(),
            timestamp = existingPost?.timestamp ?: System.currentTimeMillis(),
            likes = existingPost?.likes ?: emptyList()
        )

        val action = if (isEdit) Model.instance::updatePost else Model.instance::addPost

        action(
            post,
            {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), if (isEdit) "Recipe updated!" else "Recipe saved!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            },
            { e ->
                activity?.runOnUiThread {
                    setLoading(false)
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !loading
        binding.btnSelectImage.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }}