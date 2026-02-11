package com.jenil.librarymanagement.fragments.Start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.viewPagerAdapter
import com.jenil.librarymanagement.databinding.FragmentOnBoardingBinding


class OnBoardingFragment : Fragment() {

    private lateinit var binding : FragmentOnBoardingBinding
    private lateinit var viewPagerAdapter: viewPagerAdapter
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentOnBoardingBinding.inflate(layoutInflater)

        viewPagerAdapter = viewPagerAdapter(childFragmentManager,lifecycle)
        binding.idViewPager.adapter = viewPagerAdapter

        binding.apply {
            dotIndicator1.setBackgroundResource(R.drawable.bg_dot_select)
            dotIndicator2.setBackgroundResource(R.drawable.bg_dot)
            dotIndicator3.setBackgroundResource(R.drawable.bg_dot)
            idViewPager.setCurrentItem(currentPage ++ , true)
        }

        binding.tvGetStarted.text = getString(R.string.next)
        binding.ivBack.setOnClickListener {
            val backPage = binding.idViewPager.currentItem - 1
            if (backPage >= 0){
                binding.idViewPager.setCurrentItem(backPage, true)
            }
        }
        binding.tvSkip.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }


        binding.idViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.dotIndicator1.setBackgroundResource(R.drawable.bg_dot)
                binding.dotIndicator2.setBackgroundResource(R.drawable.bg_dot)
                binding.dotIndicator3.setBackgroundResource(R.drawable.bg_dot)

                when(position){
                    0 -> {
                        binding.dotIndicator1.setBackgroundResource(R.drawable.bg_dot_select)
                        binding.tvSkip.visibility = View.GONE
                        binding.ivBack.visibility = View.GONE
                    }
                    1 -> {
                        binding.dotIndicator2.setBackgroundResource(R.drawable.bg_dot_select)
                        binding.tvSkip.visibility = View.VISIBLE
                        binding.ivBack.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.dotIndicator3.setBackgroundResource(R.drawable.bg_dot_select)
                        binding.tvSkip.visibility = View.VISIBLE
                        binding.ivBack.visibility = View.VISIBLE
                    }
                }

                when(position){
                    0 -> {
                        binding.tvGetStarted.text = getString(R.string.next)
                    }
                    2 -> {
                        binding.tvGetStarted.text = getString(R.string.get_started)
                    }
                    else -> {
                        binding.tvGetStarted.text = getString(R.string.next)
                    }
                }

                binding.tvGetStarted.setOnClickListener {
                    val nextPage = binding.idViewPager.currentItem + 1
                    if (nextPage < viewPagerAdapter.itemCount){
                        binding.idViewPager.setCurrentItem(nextPage, true)
                    }else{
                        SharedPreference.putBoolean("isSkipBoard", true)
                        findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
                    }
                }
            }
        })

        return binding.root
    }


}