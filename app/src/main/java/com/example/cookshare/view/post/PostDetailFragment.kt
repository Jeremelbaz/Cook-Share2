package com.example.cookshare.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cookshare.databinding.FragmentPostDetailBinding

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

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

        binding.tvTitle.text = "Recipe Title"
        binding.tvUserName.text = "Author Name"
        binding.tvCategory.text = "Category"
        binding.tvPrepTime.text = "Preparation time: 30 min"
        binding.tvDescription.text = "Recipe description..."
        binding.tvIngredients.text = "• Ingredient 1\n• Ingredient 2"
        binding.tvInstructions.text = "1. Step 1\n2. Step 2"
        binding.tvNotes.text = "Additional notes..."

        binding.btnLike.setOnClickListener {
            // TODO: Like logic
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}