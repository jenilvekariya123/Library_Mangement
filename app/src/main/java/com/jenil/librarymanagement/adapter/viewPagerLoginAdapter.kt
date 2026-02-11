package com.jenil.librarymanagement.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jenil.librarymanagement.fragments.Admin.Start.AdminLoginFragment
import com.jenil.librarymanagement.fragments.User.Start.UserLoginFragment

class viewPagerLoginAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 1){
            return AdminLoginFragment()
        }
        return UserLoginFragment()
    }
}