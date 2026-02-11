package com.jenil.librarymanagement.fragments.User.View.Profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)

        binding.idAppBar.apply {
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            tvAppName.text = getString(R.string.edit_profile)
        }

        val studId = SharedPreference.getString("StudentID","0")
        setDataOnField(studId.toString())

        binding.btnSave.setOnClickListener {
            editProfileData(studId.toString())
        }

        return binding.root
    }

    private fun setDataOnField(studID : String){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child(studID.toString()).child("fullName").getValue(String::class.java)
                val phoneNumber = snapshot.child(studID.toString()).child("mobile").getValue(String::class.java)
                val emailId = snapshot.child(studID.toString()).child("email").getValue(String::class.java)

                binding.apply {
                    if (userName != null){
                        etUserName.setText(userName)
                    }else{
                        etUserName.setText(getString(R.string.not_available))
                    }

                    if (phoneNumber != null){
                        etContactNo.setText(phoneNumber)
                    }else{
                        etContactNo.setText(getString(R.string.not_available))
                    }

                    if (emailId != null){
                        etEmailAddress.setText(emailId)
                    }else{
                        etEmailAddress.setText(getString(R.string.not_available))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG","ERROR ::: ${error.message}")
            }

        })
    }

    private fun editProfileData(studID: String){
        DialogUtils.loader(requireContext(),"Loading...")

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
        val updates = mutableMapOf<String, Any>()

        updates["fullName"] = binding.etUserName.text.toString()
        updates["mobile"] = binding.etContactNo.text.toString()
        updates["email"] = binding.etEmailAddress.text.toString()

        databaseReference.child(studID.toString()).updateChildren(updates).addOnCompleteListener {
            if (it.isSuccessful){
                DialogUtils.dismissDialog()
                Toast.makeText(requireContext(),"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
            }else{
                DialogUtils.dismissDialog()
                Toast.makeText(requireContext(),it.exception!!.message,Toast.LENGTH_SHORT).show()
            }
        }


    }

}