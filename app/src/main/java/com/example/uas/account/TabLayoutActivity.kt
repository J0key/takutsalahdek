package com.example.uas.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas.R
import com.example.uas.databinding.ActivityTabLayoutBinding

class TabLayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTabLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_layout)
        binding = ActivityTabLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            viewPagerLogin.adapter = TabLayoutAdapter(supportFragmentManager)
            // Hubungkan ViewPager dengan TabLayout
            tabLayoutLogin.setupWithViewPager(viewPagerLogin)
        }
    }
}