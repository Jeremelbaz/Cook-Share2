package com.example.cookshare.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookshare.databinding.FragmentMyPostsBinding
import com.example.cookshare.model.Model
import com.example.cookshare.view.feed.PostAdapter
import com.example.cookshare.viewmodel.MyPostsViewModel

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPostsViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostAdapter(
            posts = emptyList(),
            onLikeClick = { post ->
                val userId = Model.instance.getCurrentUserId()
                val newLikes = if (post.likes.contains(userId)) post.likes - userId else post.likes + userId
                viewModel.updatePost(post.copy(likes = newLikes), {}, {})
            },
            onPostClick = { post ->
                findNavController().navigate(
                    MyPostsFragmentDirections.actionMyPostsToPostDetail(post.id)
                )
            }
        )

        binding.rvMyPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPosts.adapter = adapter

        viewModel.myPosts.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)

            if (posts.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvMyPosts.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvMyPosts.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}