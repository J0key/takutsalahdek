package com.example.uas.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas.R
import com.example.uas.databinding.ActivityMainAdminBinding

class mainAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addBtn.setOnClickListener{
            val intent = Intent(this@mainAdminActivity, AddActivity::class.java)
            startActivity(intent)
        }
    }
}