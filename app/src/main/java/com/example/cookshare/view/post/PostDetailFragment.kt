package com.example.cookshare.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cookshare.databinding.FragmentPostDetailBinding
import com.example.cookshare.model.Model
import com.squareup.picasso.Picasso

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = args.postId

        Model.instance.getAllPostsLocal().observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId } ?: return@observe

            binding.tvTitle.text = post.title
            binding.tvUserName.text = post.userName
            binding.tvCategory.text = post.category
            binding.tvPrepTime.text = "Preparation time: ${post.preparationTime}"
            binding.tvDescription.text = post.description
            binding.tvIngredients.text = post.ingredients.joinToString("\n") { "• $it" }
            binding.tvInstructions.text = post.instructions.joinToString("\n") { "• $it" }
            binding.tvNotes.text = post.notes

            if (post.imageUrl.isNotEmpty()) {
                Picasso.get().load(post.imageUrl).into(binding.imgRecipe)
            }

            binding.btnLike.setOnClickListener {
                // TODO: Like logic
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}