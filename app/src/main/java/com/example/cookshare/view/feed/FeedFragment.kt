package com.example.cookshare.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookshare.databinding.FragmentFeedBinding
import com.example.cookshare.model.Post

class FeedFragment : Fragment() {

    private var binding: FragmentFeedBinding? = null
    private lateinit var postAdapter: PostAdapter

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
        loadMockData()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            posts = emptyList(),
            onLikeClick = { post -> },
            onPostClick = { post -> }
        )
        binding?.recyclerViewFeed?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun loadMockData() {
        val mockPosts = listOf(
            Post(
                id = "1",
                title = "Spaghetti Carbonara",
                userName = "@chef_mario",
                description = "Classic Italian pasta",
                preparationTime = "20 min"
            ),
            Post(
                id = "2",
                title = "Chicken Salad",
                userName = "@rachel1112",
                description = "Healthy and delicious",
                preparationTime = "15 min"
            ),
            Post(
                id = "3",
                title = "Tiramisu",
                userName = "@dessert_lover",
                description = "Classic Italian dessert",
                preparationTime = "30 min"
            )
        )
        postAdapter.updatePosts(mockPosts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}