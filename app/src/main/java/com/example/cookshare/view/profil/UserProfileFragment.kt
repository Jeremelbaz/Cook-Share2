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
import androidx.navigation.fragment.findNavController
import com.example.cookshare.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null

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

        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvUserName.text = currentUser.displayName ?: "No name"
            binding.tvUserEmail.text = currentUser.email ?: "No email"

            if (currentUser.photoUrl != null) {
                Picasso.get().load(currentUser.photoUrl).into(binding.imgProfile)
            }
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imgProfile.setImageURI(selectedImageUri)
            uploadProfileImage()
        }
    }

    private fun uploadProfileImage() {
        val uri = selectedImageUri ?: return
        val fileName = "profiles/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUrl)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdate)
                    Toast.makeText(requireContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}