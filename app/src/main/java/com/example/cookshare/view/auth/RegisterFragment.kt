package com.example.cookshare.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookshare.databinding.FragmentRegisterBinding
import com.example.cookshare.model.Model
import com.example.cookshare.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setLoading(true)

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val firebaseUser = result.user!!

                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                    firebaseUser.updateProfile(profileUpdate)

                    val user = User(
                        uid = firebaseUser.uid,
                        fullName = fullName,
                        email = email
                    )

                    Model.instance.saveUser(
                        user = user,
                        onSuccess = {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "Account created!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(
                                    RegisterFragmentDirections.actionRegisterToLogin()
                                )
                            }
                        },
                        onFailure = {
                            activity?.runOnUiThread {
                                setLoading(false)
                                Toast.makeText(requireContext(), "Profile save failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                .addOnFailureListener {
                    setLoading(false)
                    Toast.makeText(requireContext(), "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterToLogin()
            )
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
