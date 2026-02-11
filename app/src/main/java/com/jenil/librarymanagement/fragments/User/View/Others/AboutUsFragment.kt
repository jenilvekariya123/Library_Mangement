package com.jenil.librarymanagement.fragments.User.View.Others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.BuildConfig
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.FragmentAboutUsBinding


class AboutUsFragment : Fragment() {

    private lateinit var binding: FragmentAboutUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutUsBinding.inflate(layoutInflater)

        binding.idAppBar.apply {
            tvAppName.text = getString(R.string.about_us)
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.tvVersion.text =  BuildConfig.VERSION_NAME

        return binding.root
    }


}