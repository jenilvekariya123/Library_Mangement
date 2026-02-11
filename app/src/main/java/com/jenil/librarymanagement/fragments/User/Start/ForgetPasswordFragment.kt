package com.jenil.librarymanagement.fragments.User.Start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jenil.librarymanagement.databinding.FragmentForgetPasswordBinding


class ForgetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }


}