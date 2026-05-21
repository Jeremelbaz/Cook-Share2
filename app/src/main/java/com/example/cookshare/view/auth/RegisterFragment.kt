package com.example.cookshare.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cookshare.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnRegister?.setOnClickListener {
            val email = binding?.etEmail?.text.toString().trim()
            val fullName = binding?.etFullName?.text.toString().trim()
            val password = binding?.etPassword?.text.toString().trim()
            val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

            if (email.isEmpty() || fullName.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(),
                    "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Auth — on branchera en Phase 6
        }

        binding?.tvGoToLogin?.setOnClickListener {
            // Navigation vers Login — on branchera en Phase 4
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}