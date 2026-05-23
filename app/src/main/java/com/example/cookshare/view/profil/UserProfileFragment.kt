package com.example.cookshare.view.profil

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleSelectedImage(it) }
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
            imagePicker.launch("image/*")
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
            Toast.makeText(requireContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show()
        }

        viewModel.loadCurrentUser()
    }

    private fun handleSelectedImage(uri: Uri) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}