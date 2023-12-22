package com.example.uas.users

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.R
import com.example.uas.account.TabLayoutActivity
import com.example.uas.databinding.FragmentUserHomeBinding
import com.example.uas.databinding.FragmentUserSettingsBinding
import com.example.uas.helper.Constant
import com.example.uas.helper.sharepref
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService


class UserSettingsFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentUserSettingsBinding
    private lateinit var sharedPref: sharepref

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = sharepref(requireContext())

        val sessionUsername  = sharedPref.getString(Constant.PREF_USERNAME)

        if (sessionUsername != null) {
            val userQuery = db.collection("users")
                .whereEqualTo("username", sessionUsername)
                .get()

            userQuery.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val userUsername = document.getString("username")
                        val userEmail = document.getString("email")
                        val userStatus = document.getBoolean("isUser")

                        with(binding){
                            username.text = userUsername
                            email.text = userEmail

                            logoutBtn.setOnClickListener{
                                val intent = Intent(requireContext(), TabLayoutActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding
    }
}