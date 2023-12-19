package com.example.uas.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uas.admin.MainAdminActivity
import com.example.uas.databinding.FragmentRegisterBinding
import com.example.uas.user.bottomNavbarActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    private lateinit var etUsername: EditText
    private lateinit var etSpinner: Spinner
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etUsername = binding.EditUsernameSignUp
        etSpinner = binding.role
        etEmail = binding.EditEmailSignUp
        etPassword = binding.EditPasswordSignUp

        setupSpinner()

        binding.ButtonSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun setupSpinner() {
        val roles = arrayListOf("Admin", "User")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        etSpinner.adapter = adapter
    }

    private fun registerUser() {
        val username = etUsername.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val selectedRole = etSpinner.selectedItem.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
        } else {
            val userData = HashMap<String, Any>()
                userData["username"] = username
                userData["password"] = password
                userData["role"] = selectedRole
                userData["email"] = email

                // Save user data in Firestore
                db.collection("users")
                    .add(userData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToHome(selectedRole)
                    }
                    .addOnFailureListener { d ->
                        Toast.makeText(
                            requireContext(),
                            "Error: ${d.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    private fun navigateToHome(role: String) {
        val intent = when (role) {
            "Admin" -> Intent(requireContext(), MainAdminActivity::class.java)
            // Add other role-specific activities here
            else -> Intent(requireContext(), bottomNavbarActivity::class.java)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding
    }
}
