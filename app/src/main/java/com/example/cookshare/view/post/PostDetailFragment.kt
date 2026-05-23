package com.example.cookshare.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookshare.databinding.FragmentPostDetailBinding
import com.example.cookshare.model.Comment
import com.example.cookshare.model.Model
import com.squareup.picasso.Picasso
import java.util.UUID

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()
    private lateinit var commentAdapter: CommentAdapter

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

        commentAdapter = CommentAdapter(emptyList())
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.adapter = commentAdapter

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
            binding.btnLike.text = "Like (${post.likes.size})"

            if (post.imageUrl.isNotEmpty()) {
                Picasso.get().load(post.imageUrl).into(binding.imgRecipe)
            }
        }

        loadComments(postId)

        binding.btnLike.setOnClickListener {
            Model.instance.getAllPostsLocal().value?.find { it.id == postId }?.let { post ->
                val userId = Model.instance.getCurrentUserId()
                val newLikes = if (post.likes.contains(userId)) {
                    post.likes - userId
                } else {
                    post.likes + userId
                }
                val updatedPost = post.copy(likes = newLikes)
                Model.instance.updatePost(updatedPost, {}, {})
            }
        }

        binding.btnSendComment.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val comment = Comment(
                id = UUID.randomUUID().toString(),
                postId = postId,
                userId = Model.instance.getCurrentUserId(),
                userName = Model.instance.getCurrentUserName(),
                text = text,
                timestamp = System.currentTimeMillis()
            )

            Model.instance.addComment(
                comment = comment,
                onSuccess = {
                    activity?.runOnUiThread {
                        binding.etComment.text?.clear()
                        loadComments(postId)
                    }
                },
                onFailure = {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Error sending comment", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    private fun loadComments(postId: String) {
        Model.instance.getCommentsForPost(
            postId = postId,
            onSuccess = { comments ->
                activity?.runOnUiThread {
                    commentAdapter.updateComments(comments)
                }
            },
            onFailure = {}
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}