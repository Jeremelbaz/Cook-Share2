package com.example.cookshare.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookshare.databinding.FragmentFeedBinding
import com.example.cookshare.viewmodel.PostsViewModel

class FeedFragment : Fragment() {

    private var binding: FragmentFeedBinding? = null
    private lateinit var postAdapter: PostAdapter
    private val viewModel: PostsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observePosts()
        setupButtons()
        viewModel.refreshPosts({}, {})
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            posts = emptyList(),
            onLikeClick = { post -> },
            onPostClick = { post ->
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedToPostDetail(post.id)
                )
            }
        )
        binding?.recyclerViewFeed?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun observePosts() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.updatePosts(posts)
        }
    }

    private fun setupButtons() {
        binding?.fabAddPost?.setOnClickListener {
            findNavController().navigate(
                FeedFragmentDirections.actionFeedToAddEditPost()
            )
        }

        binding?.btnSearch?.setOnClickListener {
            findNavController().navigate(
                FeedFragmentDirections.actionFeedToSearch()
            )
        }

        binding?.btnProfile?.setOnClickListener {
            findNavController().navigate(
                FeedFragmentDirections.actionFeedToProfile()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}