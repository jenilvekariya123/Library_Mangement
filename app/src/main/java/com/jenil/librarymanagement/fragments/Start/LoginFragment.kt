package com.jenil.librarymanagement.fragments.Start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.jenil.librarymanagement.adapter.viewPagerLoginAdapter
import com.jenil.librarymanagement.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewAdapter : viewPagerLoginAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      binding = FragmentLoginBinding.inflate(layoutInflater)

        viewAdapter = viewPagerLoginAdapter(childFragmentManager,lifecycle)
        binding.idViewpager1.adapter = viewAdapter

        binding.idTabLayout.addTab(binding.idTabLayout.newTab().setText("User"))
        binding.idTabLayout.addTab(binding.idTabLayout.newTab().setText("Admin"))

        binding.idTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Set the ViewPager page based on the selected tab
                binding.idViewpager1.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // You can leave this empty if no action is required when a tab is unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Leave this empty if no action is required when the tab is reselected
            }
        })


        binding.idViewpager1.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.idTabLayout.selectTab(binding.idTabLayout.getTabAt(position))
            }
        })

        return binding.root
    }

    fun SwitchToTab() {
        binding.idViewpager1.currentItem = 0
    }


}