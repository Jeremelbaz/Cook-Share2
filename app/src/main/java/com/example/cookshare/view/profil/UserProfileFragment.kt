package com.example.cookshare.view.profil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cookshare.databinding.FragmentUserProfileBinding
import com.example.cookshare.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.tvUserName.text = viewModel.getCurrentUserName()
        binding.tvUserEmail.text = viewModel.getCurrentUserEmail()

        val photoUrl = viewModel.getCurrentUserPhoto()
        if (photoUrl != null) {
            Picasso.get().load(photoUrl).into(binding.imgProfile)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnEditProfile.isEnabled = !isLoading
        }

        binding.imgProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.btnMyPosts.setOnClickListener {
            findNavController().navigate(
                UserProfileFragmentDirections.actionProfileToMyPosts()
            )
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(
                UserProfileFragmentDirections.actionProfileToLogin()
            )
        }

        binding.btnEditProfile.setOnClickListener {
            // TODO: Edit profile
        }

        viewModel.loadCurrentUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            binding.imgProfile.setImageURI(uri)

            viewModel.uploadProfilePhoto(
                uri = uri,
                onSuccess = {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}