package com.example.uas.account

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.uas.Notif.NotifReceiver
import com.example.uas.R
import com.example.uas.admin.MainAdminActivity
import com.example.uas.databinding.FragmentRegisterBinding
import com.example.uas.users.BottomNavbarActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    private val channelId = "TEST NOTIF"
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

//            notif receiver
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
            val intent = Intent(requireActivity(), NotifReceiver::class.java)
                .putExtra("MESSAGE", "Baca selengkapnya ...")
            val pendingIntent = PendingIntent.getBroadcast(
                requireActivity(),
                0,
                intent,
                flag
            )

            val channelId = "your_channel_id"  // Replace with your actual channel ID
            val builder = NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.ghibli_icon)
                .setContentTitle("Ghibli Studio")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(0, "Further Information", pendingIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Registration Successful")
                )

            val notifManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notifChannel = NotificationChannel(
                    channelId,
                    "Ghibli Studio",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notifManager.createNotificationChannel(notifChannel)
                notifManager.notify(0, builder.build())
            } else {
                notifManager.notify(0, builder.build())
            }



            }
        }
    private fun navigateToHome(role: String) {
        val intent = when (role) {
            "Admin" -> Intent(requireContext(), MainAdminActivity::class.java)
            // Add other role-specific activities here
            else -> Intent(requireContext(), BottomNavbarActivity::class.java)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding
    }
}
