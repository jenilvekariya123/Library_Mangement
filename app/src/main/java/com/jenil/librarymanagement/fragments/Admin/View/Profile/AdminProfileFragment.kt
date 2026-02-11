package com.jenil.librarymanagement.fragments.Admin.View.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.activities.MainActivity
import com.jenil.librarymanagement.databinding.FragmentAdminProfileBinding


class AdminProfileFragment : Fragment() {

    private lateinit var binding: FragmentAdminProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProfileBinding.inflate(layoutInflater)

        val organizationNo = SharedPreference.getString("adminOrganizationNo","")

        binding.apply {
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            tvAppName.text = getString(R.string.admin_profile)
            tvEditText.setOnClickListener {
                SharedPreference.putString("adminOrganizationNo",organizationNo)
                findNavController().navigate(R.id.action_adminProfileFragment_to_adminEditProfileFragment)
            }
        }

        setProfileData(organizationNo.toString())


        binding.btnLogOut.setOnClickListener {
            DialogUtils.showDialog(requireActivity(),
                "Are you sure you want to logout?","Log Out","Cancel",
                object : DialogUtils.OnDialogButtonClick{
                    override fun onPositiveButtonClick() {
                        SharedPreference.clearSharedPreference()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }

                    override fun onNegativeButtonClick() {
                        DialogUtils.dismissDialog()
                    }
                })
        }


        return binding.root
    }

    private fun setProfileData( adminNo: String) {

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val libraryName = snapshot.child("Admin").child(adminNo).child("LibraryName").getValue(String::class.java)
                val libraryNo = snapshot.child("Admin").child(adminNo).child("LibraryNumber").getValue(String::class.java)
                val Address = snapshot.child("Admin").child(adminNo).child("Address").getValue(String::class.java)
                val libraryPhone = snapshot.child("Admin").child(adminNo).child("LibraryPhone").getValue(String::class.java)

                if (libraryName != null){
                    binding.tvLibraryName.text = libraryName
                }else{
                    binding.tvLibraryName.text = "NA"
                }

                if (libraryNo != null){
                    binding.tvLibraryNo.text = libraryNo
                }else{
                    binding.tvLibraryNo.text = "NA"
                }

                if (Address != null){
                    binding.tvLibraryAddress.text = Address
                }else{
                    binding.tvLibraryAddress.text = "NA"
                }
                if (libraryPhone != null){
                    binding.tvPhoneNo.text = libraryPhone
                }else{
                    binding.tvPhoneNo.text = "NA"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG","ERROR ::: $error")
            }

        })
    }


}