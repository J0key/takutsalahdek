package com.example.uas.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.uas.R
import com.example.uas.databinding.ActivityBottomNavbarBinding
import com.example.uas.helper.sharepref
import com.example.uas.userHomeFragment
import com.example.uas.userSettingsFragment

class bottomNavbarActivity : AppCompatActivity() {

    lateinit var sharedPref: sharepref
    private lateinit var bindingPublicFragment: ActivityBottomNavbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navbar)

        bindingPublicFragment = ActivityBottomNavbarBinding.inflate(layoutInflater)
        setContentView(bindingPublicFragment.root)
        loadFragment(userHomeFragment())

        with(bindingPublicFragment){
            bottomNavigationView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.homeFragment-> {
                        loadFragment(userHomeFragment())
                        true
                    }
                    R.id.settingFragment -> {
                        loadFragment(userSettingsFragment())
                        true
                    }
                    else ->{
                        false}
                }
            }
        }
    }
    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}