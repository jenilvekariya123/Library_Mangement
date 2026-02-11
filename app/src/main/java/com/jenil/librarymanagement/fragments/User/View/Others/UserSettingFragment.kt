package com.jenil.librarymanagement.fragments.User.View.Others

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.BuildConfig
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.activities.MainActivity
import com.jenil.librarymanagement.databinding.FragmentUserSettingBinding


class UserSettingFragment : Fragment() {

    private lateinit var binding : FragmentUserSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserSettingBinding.inflate(layoutInflater)

        val studId = SharedPreference.getString("StudentID","0")

        binding.idAppBar.apply {
            tvAppName.text = getString(R.string.setting)
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.apply {
            idAboutUs.setOnClickListener {
                findNavController().navigate(R.id.action_userSettingFragment_to_aboutUsFragment)
            }

            idProfile.setOnClickListener {
                SharedPreference.putString("StudentID",studId)
                findNavController().navigate(R.id.action_userSettingFragment_to_userProfileFragment)
            }

            idLogout.setOnClickListener {
                DialogUtils.showDialog(requireActivity(),
                    "Are you sure you want to logout?","Log Out","Cancel",
                    object : DialogUtils.OnDialogButtonClick {
                        override fun onPositiveButtonClick() {
                            SharedPreference.clearSharedPreference()
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onNegativeButtonClick() {
                            DialogUtils.dismissDialog()
                        }
                    })
            }
        }
        binding.tvVersion.text = BuildConfig.VERSION_NAME

        return  binding.root
    }


}