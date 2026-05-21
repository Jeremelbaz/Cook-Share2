package com.example.cookshare.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookshare.databinding.FragmentMyPostsBinding
import com.example.cookshare.view.feed.PostAdapter

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

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
            onLikeClick = { post -> },
            onPostClick = { post -> }
        )

        binding.rvMyPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPosts.adapter = adapter

        // TODO: Load user's posts from ViewModel
        binding.tvEmpty.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}