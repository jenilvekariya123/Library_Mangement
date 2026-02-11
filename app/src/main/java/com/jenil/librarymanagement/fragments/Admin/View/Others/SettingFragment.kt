package com.jenil.librarymanagement.fragments.Admin.View.Others

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.BuildConfig
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.activities.MainActivity
import com.jenil.librarymanagement.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)

        val orgNo = SharedPreference.getString("adminOrganizationNo","")

        binding.idAppBar.tvAppName.text = getString(R.string.setting)
        binding.idAppBar.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.idProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_adminProfileFragment)
            SharedPreference.putString("adminOrganizationNo",orgNo)
        }

        binding.tvVersion.text = BuildConfig.VERSION_NAME

        binding.idLogout.setOnClickListener {
            DialogUtils.showDialog(requireActivity(),
                "Are you sure you want to logout?","Log Out","Cancel",
                object : DialogUtils.OnDialogButtonClick {
                    override fun onPositiveButtonClick() {
                        SharedPreference.clearSharedPreference()
                        val intent = Intent(requireActivity(),MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onNegativeButtonClick() {
                        DialogUtils.dismissDialog()
                    }
                })
        }

        return binding.root
    }
}