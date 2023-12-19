package com.example.uas.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uas.admin.mainAdminActivity
import com.example.uas.databinding.FragmentLoginBinding
import com.example.uas.helper.Constant
import com.example.uas.helper.sharepref
import com.example.uas.user.bottomNavbarActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPref: sharepref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = sharepref(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPref = sharepref(requireContext())
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ButtonSignIn.setOnClickListener {
            val username = binding.EditUsernameSignIn.text.toString()
            val password = binding.EditPasswordSignIn.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Please fill all the fields")
            } else {
                authenticateUser(username, password)
            }
        }
    }

    private fun authenticateUser(username: String, password: String) {
        db.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password" , password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    showToast("User not found")
                    return@addOnSuccessListener
                } else {
                    val userDoc = documents.documents[0]
                    val storedPassword = userDoc.getString("password")

                    if (password == storedPassword) {
                        navigateBasedOnRole(userDoc.getString("role"), username)
                    } else {
                        showToast("Incorrect password")
                    }
                }
            }
            .addOnFailureListener {
                showToast("Error accessing the database")
                Log.d("LoginFragment", "Error: ${it.message}")
            }
    }
    private fun navigateBasedOnRole(role: String?, username: String) {
        when (role) {
            "Admin" -> {
                saveSession(username, role)
                startActivity(Intent(requireContext(), mainAdminActivity::class.java))
                showToast("Admin Sign In Successfully.")
            }
            "User" -> {
                saveSession(username, role)
                startActivity(Intent(requireContext(), bottomNavbarActivity::class.java))
                showToast("User Sign In Successfully.")
            }
            else -> showToast("Invalid role")
        }
        activity?.finish()
    }



    private fun saveSession(username: String, role: String) {
        sharedPref.put(Constant.PREF_USERNAME, username)
        sharedPref.put(Constant.PREF_ROLE, role)
        sharedPref.put(Constant.PREF_IS_LOGIN, true)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding
    }
}
