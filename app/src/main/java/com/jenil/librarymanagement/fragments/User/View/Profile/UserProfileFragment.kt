package com.jenil.librarymanagement.fragments.User.View.Profile

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
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.databinding.FragmentUserProfileBinding


class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var dateBaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)

        val studId = SharedPreference.getString("StudentID","0")

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.tvAppName.text = getString(R.string.profile)

        binding.ivEdit.setOnClickListener {
            SharedPreference.putString("StudentID",studId.toString())
            findNavController().navigate(R.id.action_userProfileFragment_to_editProfileFragment)
        }

        fetchUserData(studId.toString())

        return binding.root
    }

    private fun fetchUserData(studId : String) {

        firebaseDatabase = FirebaseDatabase.getInstance()
        dateBaseReference = firebaseDatabase.getReference().child("Users")

        dateBaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val userName = snapshot.child(studId).child("fullName").getValue(String::class.java)
                val phoneNumber = snapshot.child(studId).child("mobile").getValue(String::class.java)
                val email = snapshot.child(studId).child("email").getValue(String::class.java)
                val studID = snapshot.child(studId).child("studId").getValue(String::class.java)

                binding.apply {
                    if (userName != null){
                        tvUserName.text = userName
                    }else{
                        tvUserName.text = getString(R.string.not_available)
                    }

                    if (phoneNumber != null){
                        tvPhoneNum.text = getString(R.string._91, phoneNumber)
                    }else{
                        tvPhoneNum.text = getString(R.string.not_available)
                    }

                    if (email != null){
                        tvEmail.text = email
                    }else{
                        tvEmail.text = getString(R.string.not_available)
                    }

                    if (studID != null){
                        tvStudId.text = studID
                    }else{
                        tvStudId.text = getString(R.string.not_available)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG","ERROR ::: ${error.message}")
            }

        })

    }

}