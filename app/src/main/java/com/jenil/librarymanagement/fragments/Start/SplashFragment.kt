package com.jenil.librarymanagement.fragments.Start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.jenil.librarymanagement.activities.HomeActivity
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.activities.AdminHomeActivity
import com.jenil.librarymanagement.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)


        val isSkipBoard: Boolean = SharedPreference.getBoolean("isSkipBoard")
        val isAdminLogin: Boolean = SharedPreference.getBoolean("isAdminLogin")
        val isUserLogin: Boolean = SharedPreference.getBoolean("isUserLogin")

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdminLogin) {
                val intent = Intent(requireContext(), AdminHomeActivity::class.java)
                startActivity(intent)
            } else if (isUserLogin) {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
            } else if (isSkipBoard) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
            }
        }, 3000)

        return binding.root
    }


}