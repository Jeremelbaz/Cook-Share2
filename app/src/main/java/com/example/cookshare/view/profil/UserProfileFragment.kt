package com.example.cookshare.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cookshare.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

    private var binding: FragmentUserProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnMyRecipes?.setOnClickListener {
            // Navigation vers mes recettes — Phase 4
        }

        binding?.btnSavedRecipes?.setOnClickListener {
            // Navigation vers recettes sauvegardées — Phase 4
        }

        binding?.btnEditProfile?.setOnClickListener {
            // Edition du profil — Phase 6
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}