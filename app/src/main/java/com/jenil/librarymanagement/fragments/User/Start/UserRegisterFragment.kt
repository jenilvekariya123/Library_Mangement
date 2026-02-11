package com.jenil.librarymanagement.fragments.User.Start

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.FragmentUserRegisterBinding
import com.jenil.librarymanagement.Data.userInfo

class UserRegisterFragment : Fragment() {

    private lateinit var binding: FragmentUserRegisterBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userInfo: userInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserRegisterBinding.inflate(layoutInflater)

        userInfo = userInfo()

        val fullName = binding.etFullName
        val email = binding.etEmail
        val studId = binding.etStudId
        val phoneNo = binding.etPhone
        val password = binding.etPassword

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_userRegisterFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener {
            if (checkAllField(fullName, email, studId, phoneNo, password)) {
                userRegister(fullName, email, studId, phoneNo, password)
                clearFields(fullName, email, studId, phoneNo, password)
            }
        }

        return binding.root
    }

    private fun clearFields(FullName: EditText, Email: EditText, StudId: EditText, phoneNo: EditText, Password: EditText) {

        StudId.setText("")
        FullName.setText("")
        Email.setText("")
        phoneNo.setText("")
        Password.setText("")
    }
    private fun checkAllField(FullName: EditText, Email: EditText, StudId: EditText, phoneNo: EditText, Password: EditText
    ): Boolean {
        var valid = true
        if (FullName.text.toString().isEmpty()) {
            FullName.error = "Enter Full Name"
            FullName.requestFocus()
            valid = false
        }
        if (Email.text.toString().isEmpty()) {
            Email.error = "Enter Email Address"
            Email.requestFocus()
            valid = false
        }
        if (StudId.text.toString().isEmpty()) {
            StudId.error = "Enter Email Address"
            StudId.requestFocus()
            valid = false
        }
        if (phoneNo.text.toString().isEmpty()) {
            phoneNo.error = "Enter Phone Number"
            phoneNo.requestFocus()
            valid = false
        }
        if (Password.text.toString().isEmpty()) {
            Password.error = "Enter Password"
            Password.requestFocus()
            valid = false
        }
        return true
    }

    private fun userRegister(
        FullName: EditText,
        Email: EditText,
        StudId: EditText,
        phoneNo: EditText,
        Password: EditText
    ) {
        if (binding.etPassword.text!!.length > 6){
            firebaseDatabase = FirebaseDatabase.getInstance()
            databaseReference = firebaseDatabase.getReference().child("Users").child(StudId.text.toString())
            userInfo.apply {
                fullName = FullName.text.toString().trim()
                studId = StudId.text.toString().trim()
                email = Email.text.toString().trim()
                mobile = phoneNo.text.toString().trim()
                password = Password.text.toString().trim()
            }

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded){
                        if (snapshot.hasChild(StudId.text.toString())){
                            Toast.makeText(requireContext(), "User Already Exists", Toast.LENGTH_SHORT).show()
                        }else{
                            databaseReference.child(StudId.text.toString()).setValue(userInfo)
                            Toast.makeText(requireContext(), "Register Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DatabaseError", "Error: ${error.message}")
                }

            })
        }
    }
}