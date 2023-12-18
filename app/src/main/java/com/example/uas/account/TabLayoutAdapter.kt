package com.example.uas.account

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.uas.account.LoginFragment
import com.example.uas.account.RegisterFragment

class TabLayoutAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    // menerima parameter position yang merupakan indeks tab yang dipilih
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment()
            1 -> RegisterFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
    // mengembalikan jumlah tab yang tersedia
    override fun getCount(): Int {
        return 2
    }
    // menerima parameter position yang sama dengan method getItem
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Sign In"
            1 -> "Sign Up"
            else -> null
        }
    }
}